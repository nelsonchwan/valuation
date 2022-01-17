package org.nelson.valuation.orchestrator;

import org.nelson.valuation.factory.CustomThreadFactory;
import org.nelson.valuation.model.ValuationCommand;
import org.nelson.valuation.model.ValuationRequest;
import org.nelson.valuation.model.ValuationResult;
import org.nelson.valuation.orchestrator.strategy.DefaultExecutionStrategy;
import org.nelson.valuation.orchestrator.strategy.IExecutionStrategy;
import org.nelson.valuation.util.ValuationLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ValuationOrchestrator {

    private int numValuationRequestProcessingService = 1;

    // thread pool for valuation request process tasks
    private ExecutorService valuationRequestProcessingService;

    private BlockingQueue<ValuationRequest> valuationRequestQueue;
    private BlockingQueue<ValuationCommand> fastCalculationQueue;
    private BlockingQueue<ValuationCommand> slowCalculationQueue;
    private BlockingQueue<ValuationResult> resultQueue;

    // execution strategy
    private IExecutionStrategy executionStrategy;

    private List<Future> taskList;

    private boolean isStarted = false;


    public ValuationOrchestrator(
            BlockingQueue<ValuationRequest> valuationRequestQueue,
            BlockingQueue<ValuationCommand> fastCalculationQueue,
            BlockingQueue<ValuationCommand> slowCalculationQueue,
            BlockingQueue<ValuationResult> resultQueue
    ) {
        this(valuationRequestQueue, fastCalculationQueue, slowCalculationQueue, resultQueue, new DefaultExecutionStrategy());
    }

    public ValuationOrchestrator(
            BlockingQueue<ValuationRequest> valuationRequestQueue,
            BlockingQueue<ValuationCommand> fastCalculationQueue,
            BlockingQueue<ValuationCommand> slowCalculationQueue,
            BlockingQueue<ValuationResult> resultQueue,
            IExecutionStrategy executionStrategy
    ) {
        this.valuationRequestQueue = valuationRequestQueue;
        this.fastCalculationQueue = fastCalculationQueue;
        this.slowCalculationQueue = slowCalculationQueue;
        this.resultQueue = resultQueue;
        this.executionStrategy = executionStrategy;

        taskList = new ArrayList<>(numValuationRequestProcessingService);
    }

    public synchronized void start() {
        if (!isStarted) {
            ValuationLogger.info("Orchestrator starting...");

            valuationRequestProcessingService = Executors.newFixedThreadPool(
                    numValuationRequestProcessingService,
                    new CustomThreadFactory("Request Processor ")
            );

            for (int i=0; i<numValuationRequestProcessingService; i++) {
                Future submittedTask = valuationRequestProcessingService.submit(new ValuationRequestProcessingTask(
                        valuationRequestQueue,
                        fastCalculationQueue,
                        slowCalculationQueue,
                        executionStrategy
                ));
                taskList.add(submittedTask);

            }

            isStarted = true;
            ValuationLogger.info("Orchestrator started!");
        }
    }

    public synchronized void stop() {
        if (isStarted) {
            taskList.forEach(task -> task.cancel(true));
            valuationRequestProcessingService.shutdown();
            isStarted = false;
        }
    }

    public boolean isStarted() {
        return isStarted;
    }

}
