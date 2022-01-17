package org.nelson.valuation.engine;

import org.nelson.valuation.exception.CalculationException;
import org.nelson.valuation.model.ValuationCommand;
import org.nelson.valuation.model.ValuationResult;
import org.nelson.valuation.model.ValuationResultStatus;
import org.nelson.valuation.util.ValuationLogger;

import java.util.concurrent.BlockingQueue;

public class ValuationEngine implements Runnable {

    // indicate this engine is managed by which engine manager
    private String engineManagerName;

    // queue to read from
    private BlockingQueue<ValuationCommand> calculationQueue;

    // queue to send to
    private BlockingQueue<ValuationResult> resultQueue;

    public ValuationEngine(
            String engineManagerName,
            BlockingQueue<ValuationCommand> calculationQueue,
            BlockingQueue<ValuationResult> resultQueue
    ) {
        this.engineManagerName = engineManagerName;
        this.calculationQueue = calculationQueue;
        this.resultQueue = resultQueue;
    }

    @Override
    public void run() {

        // infinite runLoop
        while (true) {

            ValuationCommand valuationCommand = null;
            try {
                // read from Valuation Command Queue
                // block if no request found
                valuationCommand = readFromInputQueue();

                // Perform calculation
                ValuationResult valuationResult = performCalculation(valuationCommand);

                // send to Result queue
                resultQueue.offer(valuationResult);

            } catch (CalculationException e) {
                // send failed result to Result queue
                resultQueue.offer(createErrorResult(valuationCommand, e));

            } catch (InterruptedException e) {
                ValuationLogger.info(" interrupted!");
                break;
            }

        }

        ValuationLogger.info("Engine is shut down!");

    }

    private ValuationCommand readFromInputQueue() throws InterruptedException {
        ValuationLogger.info("Reading from queue...");
        ValuationCommand valuationCommand = calculationQueue.take();
        ValuationLogger.info("Got item from queue: %d %s",
                valuationCommand.getValuationRequest().getRequestId(),
                valuationCommand.getValuationRequest().getProductId()
        );
        return valuationCommand;
    }

    private ValuationResult performCalculation(ValuationCommand valuationCommand) throws CalculationException, InterruptedException {
        ValuationLogger.info("Performing calculation for %d %s ...",
                valuationCommand.getValuationRequest().getRequestId(),
                valuationCommand.getValuationRequest().getProductId()
        );
        ValuationResult valuationResult = new ValuationResult();
        valuationResult.setRequestId(valuationCommand.getValuationRequest().getRequestId());
        valuationResult.setProductId(valuationCommand.getValuationRequest().getProductId());
        valuationResult.setProductType(valuationCommand.getValuationRequest().getProductType());
        valuationResult.setStatus(ValuationResultStatus.SUCCESS);
        valuationResult.setResult(
                valuationCommand.getCalculation().apply(valuationCommand)
        );
        ValuationLogger.info("Completed calculation for %d %s !",
                valuationCommand.getValuationRequest().getRequestId(),
                valuationCommand.getValuationRequest().getProductId()
        );
        return valuationResult;
    }

    private ValuationResult createErrorResult(ValuationCommand valuationCommand, CalculationException exception) {
        ValuationResult valuationResult = new ValuationResult();
        valuationResult.setRequestId(valuationCommand.getValuationRequest().getRequestId());
        valuationResult.setProductId(valuationCommand.getValuationRequest().getProductId());
        valuationResult.setProductType(valuationCommand.getValuationRequest().getProductType());
        valuationResult.setStatus(ValuationResultStatus.FAILED);
        valuationResult.setErrorMessage(exception.getMessage());
        return valuationResult;
    }

}
