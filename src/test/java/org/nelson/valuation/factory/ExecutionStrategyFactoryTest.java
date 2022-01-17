package org.nelson.valuation.factory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nelson.valuation.orchestrator.strategy.DefaultExecutionStrategy;
import org.nelson.valuation.orchestrator.strategy.IExecutionStrategy;
import org.nelson.valuation.orchestrator.strategy.ProductFastSlowExecutionStrategy;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionStrategyFactoryTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void defaultStretegy() {
        IExecutionStrategy executionStrategy = ExecutionStrategyFactory.defaultStretegy();
        assertTrue(executionStrategy instanceof DefaultExecutionStrategy);
    }

    @Test
    void productFastSlowStrategy() {
        IExecutionStrategy executionStrategy = ExecutionStrategyFactory.productFastSlowStrategy();
        assertTrue(executionStrategy instanceof ProductFastSlowExecutionStrategy);
    }

}