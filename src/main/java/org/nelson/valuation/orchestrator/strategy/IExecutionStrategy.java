package org.nelson.valuation.orchestrator.strategy;

import org.nelson.valuation.model.ValuationCommand;
import org.nelson.valuation.model.ValuationRequest;

public interface IExecutionStrategy {

    ValuationCommand generateCommand(ValuationRequest request);

}
