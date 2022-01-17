package org.nelson.valuation;

import org.apache.commons.lang3.time.StopWatch;
import org.nelson.valuation.engine.EngineManager;
import org.nelson.valuation.factory.ExecutionStrategyFactory;
import org.nelson.valuation.model.ValuationCommand;
import org.nelson.valuation.model.ValuationRequest;
import org.nelson.valuation.model.ValuationResult;
import org.nelson.valuation.orchestrator.ValuationOrchestrator;
import org.nelson.valuation.model.ProductType;
import org.nelson.valuation.util.ValuationLogger;

import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class ValuationTestApp {

    private static final int MESSAGE_QUEUE_SIZE = 2500;

    private BlockingQueue<ValuationRequest> valuationRequestQueue;
    private BlockingQueue<ValuationCommand> fastCalculationQueue;
    private BlockingQueue<ValuationCommand> slowCalculationQueue;
    private BlockingQueue<ValuationResult> resultQueue;

    private ValuationOrchestrator valuationOrchestrator;
    private EngineManager localEngineManager;
    private EngineManager gridEngineManager;

    private Thread listenerThread;

    private AtomicInteger requestId;
    private List<ValuationRequest> fastRequests;
    private List<ValuationRequest> slowRequests;

    private StopWatch stopWatchForFastRequests;
    private StopWatch stopWatchForSlowRequests;


    public void startRequestProducers(int fastRequestCount, int slowRequestCount) throws InterruptedException {
        requestId = new AtomicInteger(1);
        fastRequests = new CopyOnWriteArrayList<>();
        slowRequests = new CopyOnWriteArrayList<>();
        Thread fastProducer = new Thread( new RequestsProducer(ProductType.VANILLA, fastRequestCount, fastRequests) );
        Thread slowProducer = new Thread( new RequestsProducer(ProductType.EXOTIC, slowRequestCount, slowRequests) );
        fastProducer.setName("Fast Requests Producer");
        slowProducer.setName("Slow Requests Producer");
        fastProducer.start();
        slowProducer.start();
        fastProducer.join();
        slowProducer.join();
        ValuationLogger.info("Fast Requests size: %d", fastRequests.size());
        ValuationLogger.info("Slow Requests size: %d", fastRequests.size());
    }

    public ValuationRequest generateRequest(ProductType productType) {
        int id = requestId.getAndIncrement();
        ValuationRequest valuationRequest = new ValuationRequest(
                id,
                productType == ProductType.VANILLA ? "V" + id : "E" + id,
                productType
        );
        return valuationRequest;
    }

    public void sendRequest(ValuationRequest request) {
        valuationRequestQueue.offer(request);
    }

    public void warmUpJvm() throws InterruptedException {
        ValuationLogger.info("################## Warming up JVM ... ##################");

        // warm up with fast slow strategy
        setupValuationEnvironment(false);
        startRequestProducers(2, 2);
        stopWatchForFastRequests = new StopWatch();
        stopWatchForSlowRequests = new StopWatch();
        stopWatchForFastRequests.start();
        stopWatchForSlowRequests.start();
        startEnvironment();
        startResultListener();
        getListenerThread().join();

        ValuationLogger.info("################## Warmed up JVM ! ##################");
    }

    public void testDefaultSetup() throws InterruptedException {
        ValuationLogger.info("################## Default env started ... ##################");

        setupValuationEnvironment(true);
        startRequestProducers(10, 10);
        stopWatchForFastRequests = new StopWatch();
        stopWatchForSlowRequests = new StopWatch();
        stopWatchForFastRequests.start();
        stopWatchForSlowRequests.start();
        startEnvironment();
        startResultListener();
        getListenerThread().join();

        ValuationLogger.info("################## Default env stopped ! ##################");
    }

    public void testFastSlowSetup() throws InterruptedException {
        ValuationLogger.info("################## Fast Slow env started ... ##################");

        setupValuationEnvironment(false);
        startRequestProducers(10, 10);
        stopWatchForFastRequests = new StopWatch();
        stopWatchForSlowRequests = new StopWatch();
        stopWatchForFastRequests.start();
        stopWatchForSlowRequests.start();
        startEnvironment();
        startResultListener();
        getListenerThread().join();

        ValuationLogger.info("################## Fast Slow env stopped ! ##################");
    }

    public void setupValuationEnvironment(boolean isDefaultExecution) {

        // setup all Message Queues
        valuationRequestQueue = new LinkedBlockingQueue<>(MESSAGE_QUEUE_SIZE);
        fastCalculationQueue = new LinkedBlockingQueue<>(MESSAGE_QUEUE_SIZE);
        slowCalculationQueue = new LinkedBlockingQueue<>(MESSAGE_QUEUE_SIZE);
        resultQueue = new LinkedBlockingQueue<>(MESSAGE_QUEUE_SIZE);

        // create Valuation Orchestrator
        valuationOrchestrator = new ValuationOrchestrator(
                valuationRequestQueue,
                fastCalculationQueue,
                slowCalculationQueue,
                resultQueue,
                isDefaultExecution ? ExecutionStrategyFactory.defaultStretegy() : ExecutionStrategyFactory.productFastSlowStrategy()
        );

        if (isDefaultExecution) {
            localEngineManager = null;

            // create Grid Engine Manager
            gridEngineManager = new EngineManager(
                    "Grid Engine Manager", 4, slowCalculationQueue, resultQueue
            );
        }
        else {
            // create Local Engine Manager
            localEngineManager = new EngineManager(
                    "Local Engine Manager", 1, fastCalculationQueue, resultQueue
            );

            // create Grid Engine Manager
            gridEngineManager = new EngineManager(
                    "Grid Engine Manager", 3, slowCalculationQueue, resultQueue
            );
        }

    }

    public void startEnvironment() {
        valuationOrchestrator.start();
        if (localEngineManager != null) {
            localEngineManager.start();
        }
        gridEngineManager.start();
    }

    public void stopEnvironment() {
        if (localEngineManager != null) {
            localEngineManager.stop();
        }
        gridEngineManager.stop();
        valuationOrchestrator.stop();
    }

    public void startResultListener() {
        listenerThread = new Thread(new ResultListener());
        listenerThread.setName("Result Listener");
        listenerThread.start();
    }

    public Thread getListenerThread() {
        return listenerThread;
    }

    class RequestsProducer implements Runnable {
        ProductType productType;
        int requestCount;
        List<ValuationRequest> requests;

        Random random = new Random();

        public RequestsProducer(ProductType productType, int requestCount, List<ValuationRequest> requests) {
            this.productType = productType;
            this.requestCount = requestCount;
            this.requests = requests;
        }

        @Override
        public void run() {
            IntStream.range(0, requestCount).forEach(i -> {
                ValuationRequest request = generateRequest(productType);
                requests.add(request);
                sendRequest(request);
                try {
                    Thread.sleep(random.nextInt(5) * 500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    class ResultListener implements Runnable {
        @Override
        public void run() {
            // Read from the result queue
            int fastResultReceived = 0;
            int slowResultReceived = 0;

            while (true) {
                try {

                    ValuationResult valuationResult = resultQueue.take();
                    if (valuationResult.getProductType() == ProductType.VANILLA) {
                        ValuationLogger.info("Client received fast result: " + valuationResult);
                        ++fastResultReceived;
                        if (fastResultReceived == fastRequests.size()) {
                            stopWatchForFastRequests.stop();
                            long elapsedTime = stopWatchForFastRequests.getTime(TimeUnit.SECONDS);
                            ValuationLogger.info("All fast requests done! Elapsed: %d seconds", elapsedTime);
                        }
                    }
                    else {
                        ValuationLogger.info("Client received slow result: " + valuationResult);
                        ++slowResultReceived;
                        if (slowResultReceived == slowRequests.size()) {
                            stopWatchForSlowRequests.stop();
                            long elapsedTime = stopWatchForSlowRequests.getTime(TimeUnit.SECONDS);
                            ValuationLogger.info("All slow requests done! Elapsed: %d seconds", elapsedTime);
                        }
                    }


                    if (fastResultReceived + slowResultReceived == fastRequests.size() + slowRequests.size()) {
                        ValuationLogger.info("We have got all results! ");
                        stopEnvironment();
                        break;
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }



    public static void main(String[] args) throws InterruptedException {
        ValuationTestApp testApp = new ValuationTestApp();

        // ### Before doing real tests, warm up the JVM first
        testApp.warmUpJvm();

        // ### Run test on default strategy,
        // ### where both fast and slow requests are queued in a single message queue
        // ### they will be picked up by a single Engine Manager
        testApp.testDefaultSetup();

        // ### Run test on fast slow strategy,
        // ### where fast and slow requests are queued in a separate message queues
        // ### they will be picked up by different Engine Managers
        testApp.testFastSlowSetup();

    }

}
