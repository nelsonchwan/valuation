package org.nelson.valuation.orchestrator.strategy;

import org.nelson.valuation.model.ProductType;
import org.nelson.valuation.model.ValuationCommand;
import org.nelson.valuation.model.ValuationRequest;

public class ProductFastSlowExecutionStrategy implements IExecutionStrategy {

    @Override
    public ValuationCommand generateCommand(ValuationRequest request) {
        ValuationCommand valuationCommand = new ValuationCommand();
        valuationCommand.setValuationRequest(request);
        if (request.getProductType() == ProductType.VANILLA) {
            valuationCommand.setFastCalculation(true);
        }
        else {
            valuationCommand.setFastCalculation(false);
        }
        return valuationCommand;
    }

}
