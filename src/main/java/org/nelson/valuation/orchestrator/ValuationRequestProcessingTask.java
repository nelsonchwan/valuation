package org.nelson.valuation.orchestrator;

import org.nelson.valuation.calc.CalculationFunctionSelector;
import org.nelson.valuation.enricher.MarketDataEnricher;
import org.nelson.valuation.enricher.ProductDataEnricher;
import org.nelson.valuation.model.ValuationCommand;
import org.nelson.valuation.model.ValuationRequest;
import org.nelson.valuation.orchestrator.strategy.IExecutionStrategy;
import org.nelson.valuation.util.ValuationLogger;

import java.util.concurrent.BlockingQueue;

public class ValuationRequestProcessingTask implements Runnable {

    // read from queue
    private BlockingQueue<ValuationRequest> valuationRequestQueue;

    // send to queues
    private BlockingQueue<ValuationCommand> fastCalculationQueue;
    private BlockingQueue<ValuationCommand> slowCalculationQueue;

    // execution strategy
    private IExecutionStrategy executionStrategy;

    public ValuationRequestProcessingTask(
            BlockingQueue<ValuationRequest> valuationRequestQueue,
            BlockingQueue<ValuationCommand> fastCalculationQueue,
            BlockingQueue<ValuationCommand> slowCalculationQueue,
            IExecutionStrategy executionStrategy
    ) {
        this.valuationRequestQueue = valuationRequestQueue;
        this.fastCalculationQueue = fastCalculationQueue;
        this.slowCalculationQueue = slowCalculationQueue;
        this.executionStrategy = executionStrategy;
    }

    @Override
    public void run() {

        // infinite runLoop
        while (true) {

            try {
                // read from Valuation Request Queue
                // block if no request found
                ValuationRequest valuationRequest = readFromInputQueue();

                // generate command based on execution strategy
                ValuationCommand valuationCommand = executionStrategy.generateCommand(valuationRequest);

                // enrich with product data
                valuationCommand.setProductData( ProductDataEnricher.enrich(valuationCommand) );

                // enrich with market data
                valuationCommand.setMarketData( MarketDataEnricher.enrich(valuationCommand) );

                // select the calculation function
                valuationCommand.setCalculation(CalculationFunctionSelector.select(valuationCommand.getProductData().getValuationModel()) );

                // send to Fast / Slow queue
                sendToOutputQueue(valuationCommand);

            } catch (InterruptedException e) {
                ValuationLogger.info(" interrupted!");
                break;
            }

        }

        ValuationLogger.info("Request Processor is shut down!");

    }

    private ValuationRequest readFromInputQueue() throws InterruptedException {
        ValuationLogger.info("Reading from Valuation Request Queue...");
        ValuationRequest valuationRequest = valuationRequestQueue.take();
        ValuationLogger.info("Got item from Valuation Request Queue: %d %s",
                valuationRequest.getRequestId(),
                valuationRequest.getProductId()
        );
        return valuationRequest;
    }

    private void sendToOutputQueue(ValuationCommand valuationCommand) {
        if (valuationCommand.isFastCalculation()) {
            fastCalculationQueue.offer(valuationCommand);
        }
        else {
            slowCalculationQueue.offer(valuationCommand);
        }
    }

}
