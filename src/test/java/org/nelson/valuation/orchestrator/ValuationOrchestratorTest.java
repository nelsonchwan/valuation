package org.nelson.valuation.orchestrator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nelson.valuation.factory.ExecutionStrategyFactory;
import org.nelson.valuation.model.ValuationCommand;
import org.nelson.valuation.model.ValuationRequest;
import org.nelson.valuation.model.ValuationResult;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

class ValuationOrchestratorTest {

    static final int MESSAGE_QUEUE_SIZE = 2500;

    BlockingQueue<ValuationRequest> valuationRequestQueue;
    BlockingQueue<ValuationCommand> fastCalculationQueue;
    BlockingQueue<ValuationCommand> slowCalculationQueue;
    BlockingQueue<ValuationResult> resultQueue;

    ValuationOrchestrator valuationOrchestrator;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testStartStop() {
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
                ExecutionStrategyFactory.productFastSlowStrategy()
        );
        valuationOrchestrator.start();
        assertTrue(valuationOrchestrator.isStarted());

        valuationOrchestrator.stop();
        assertFalse(valuationOrchestrator.isStarted());

    }

}