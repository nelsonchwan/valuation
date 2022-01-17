package org.nelson.valuation.factory;

import org.nelson.valuation.orchestrator.strategy.DefaultExecutionStrategy;
import org.nelson.valuation.orchestrator.strategy.IExecutionStrategy;
import org.nelson.valuation.orchestrator.strategy.ProductFastSlowExecutionStrategy;

public class ExecutionStrategyFactory {

    private ExecutionStrategyFactory() {
    }

    public static IExecutionStrategy defaultStretegy() {
        return new DefaultExecutionStrategy();
    }

    public static IExecutionStrategy productFastSlowStrategy() {
        return new ProductFastSlowExecutionStrategy();
    }

}
