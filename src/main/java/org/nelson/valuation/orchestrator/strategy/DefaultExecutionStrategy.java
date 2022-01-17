package org.nelson.valuation.orchestrator.strategy;

import org.nelson.valuation.model.ValuationCommand;
import org.nelson.valuation.model.ValuationRequest;

public class DefaultExecutionStrategy implements IExecutionStrategy {

    @Override
    public ValuationCommand generateCommand(ValuationRequest request) {
        ValuationCommand valuationCommand = new ValuationCommand();
        valuationCommand.setValuationRequest(request);
        valuationCommand.setFastCalculation(false);
        return valuationCommand;
    }

}
