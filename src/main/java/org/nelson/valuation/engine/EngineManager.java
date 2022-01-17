package org.nelson.valuation.engine;

import org.nelson.valuation.factory.CustomThreadFactory;
import org.nelson.valuation.model.ValuationCommand;
import org.nelson.valuation.model.ValuationResult;
import org.nelson.valuation.util.ValuationLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EngineManager {

    private final String engineManagerName;

    private int numEngines;

    // thread pool for valuation request process tasks
    private ExecutorService valuationEngineService;

    // queues
    private BlockingQueue<ValuationCommand> inQueue;
    private BlockingQueue<ValuationResult> resultQueue;

    private List<Future> engineTaskList;

    private boolean isStarted = false;


    public EngineManager(
            String engineManagerName,
            int numEngines,
            BlockingQueue<ValuationCommand> inQueue,
            BlockingQueue<ValuationResult> resultQueue
    ) {
        this.engineManagerName = engineManagerName;
        this.numEngines = numEngines;
        this.inQueue = inQueue;
        this.resultQueue = resultQueue;

        engineTaskList = new ArrayList<>(numEngines);
    }

    public synchronized void start() {
        if (!isStarted) {
            ValuationLogger.info(engineManagerName + " starting...");

            valuationEngineService = Executors.newFixedThreadPool(
                    numEngines,
                    new CustomThreadFactory(engineManagerName + " engine #")
            );

            for (int i=0; i<numEngines; i++) {
                Future submittedTask = valuationEngineService.submit(new ValuationEngine(
                        engineManagerName,
                        inQueue,
                        resultQueue
                ));
                engineTaskList.add(submittedTask);
            }

            isStarted = true;
            ValuationLogger.info(engineManagerName + " started!");
        }
    }

    public synchronized void stop() {
        if (isStarted) {
            engineTaskList.forEach(task -> task.cancel(true));
            valuationEngineService.shutdown();
            isStarted = false;
        }
    }

    public boolean isStarted() {
        return isStarted;
    }

}
