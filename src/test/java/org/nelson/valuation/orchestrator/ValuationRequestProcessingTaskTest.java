package org.nelson.valuation.orchestrator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nelson.valuation.model.ProductType;
import org.nelson.valuation.model.ValuationCommand;
import org.nelson.valuation.model.ValuationRequest;
import org.nelson.valuation.orchestrator.strategy.DefaultExecutionStrategy;
import org.nelson.valuation.orchestrator.strategy.ProductFastSlowExecutionStrategy;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ValuationRequestProcessingTaskTest {

    ValuationRequestProcessingTask requestProcessor;
    BlockingQueue<ValuationRequest> valuationRequestQueue;
    BlockingQueue<ValuationCommand> fastCalculationQueue;
    BlockingQueue<ValuationCommand> slowCalculationQueue;

    @BeforeEach
    void setUp() {
        valuationRequestQueue = new LinkedBlockingQueue<>(10);
        fastCalculationQueue = new LinkedBlockingQueue<>(10);
        slowCalculationQueue = new LinkedBlockingQueue<>(10);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void runUponVanillaRequestReceivedForDefaultStrategy() throws InterruptedException {
        valuationRequestQueue.offer( vanillaProductCalcRequest() );
        setupRequestProcessorWithDefaultStrategy();
        Thread processor = new Thread(requestProcessor);
        processor.start();
        ValuationCommand command = slowCalculationQueue.poll(1, TimeUnit.MINUTES);
        processor.interrupt();
        assertNotNull(command);
        assertNotNull(command.getValuationRequest());
        assertNotNull(command.getProductData());
        assertNotNull(command.getMarketData());
        assertNotNull(command.getCalculation());
    }

    @Test
    void runUponVanillaRequestReceivedForFastSlowStrategy() throws InterruptedException {
        valuationRequestQueue.offer( vanillaProductCalcRequest() );
        setupRequestProcessorWithFastSlowStrategy();
        Thread processor = new Thread(requestProcessor);
        processor.start();
        ValuationCommand command = fastCalculationQueue.poll(1, TimeUnit.MINUTES);
        processor.interrupt();
        assertNotNull(command);
        assertNotNull(command.getValuationRequest());
        assertNotNull(command.getProductData());
        assertNotNull(command.getMarketData());
        assertNotNull(command.getCalculation());
    }

    ValuationRequest vanillaProductCalcRequest() {
        return new ValuationRequest(1, "V1", ProductType.VANILLA);
    }

    void setupRequestProcessorWithDefaultStrategy() {
        requestProcessor = new ValuationRequestProcessingTask(
                valuationRequestQueue,
                fastCalculationQueue,
                slowCalculationQueue,
                new DefaultExecutionStrategy()
        );
    }

    void setupRequestProcessorWithFastSlowStrategy() {
        requestProcessor = new ValuationRequestProcessingTask(
                valuationRequestQueue,
                fastCalculationQueue,
                slowCalculationQueue,
                new ProductFastSlowExecutionStrategy()
        );
    }

}