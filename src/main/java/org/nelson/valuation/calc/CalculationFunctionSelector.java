package org.nelson.valuation.calc;

import org.nelson.valuation.exception.CalculationException;
import org.nelson.valuation.model.ValuationCommand;
import org.nelson.valuation.model.ValuationModel;

import java.util.Random;

public class CalculationFunctionSelector {

    private static final Random random = new Random();

    private CalculationFunctionSelector() {
    }

    public static CalculationFunction<ValuationCommand, Double> select(ValuationModel valuationModel) {

        final long MIN_EXOTIC_RUNTIME_MS = 30000; // 30s

        if (valuationModel == ValuationModel.BSM_MODEL) {

            return (command) -> {
                if (command.getMarketData().getUnderlyingSpotPrice() == 0) {
                    throw new CalculationException("Underlying Spot Price is 0!");
                }
                Thread.sleep(random.nextInt(5) * 1000);
                return command.getMarketData().getUnderlyingSpotPrice() + 1;
            };

        }
        else {

            return (command) -> {
                if (command.getMarketData().getUnderlyingSpotPrice() == 0) {
                    throw new CalculationException("Underlying Spot Price is 0!");
                }
                Thread.sleep(MIN_EXOTIC_RUNTIME_MS + random.nextInt(20) * 1000);
                return Math.pow(command.getMarketData().getUnderlyingSpotPrice(), 2);
            };

        }

    }

}
