package org.nelson.valuation.engine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nelson.valuation.model.ValuationCommand;
import org.nelson.valuation.model.ValuationRequest;
import org.nelson.valuation.model.ValuationResult;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

class EngineManagerTest {

    static final int MESSAGE_QUEUE_SIZE = 2500;

    BlockingQueue<ValuationCommand> fastCalculationQueue;
    BlockingQueue<ValuationResult> resultQueue;

    EngineManager localEngineManager;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testStartStop() {
        fastCalculationQueue = new LinkedBlockingQueue<>(MESSAGE_QUEUE_SIZE);
        localEngineManager = new EngineManager(
                "Local Engine Manager", 1, fastCalculationQueue, resultQueue
        );
        localEngineManager.start();
        assertTrue(localEngineManager.isStarted());
        localEngineManager.stop();
        assertFalse(localEngineManager.isStarted());
    }
}