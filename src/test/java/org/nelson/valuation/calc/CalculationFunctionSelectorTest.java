package org.nelson.valuation.calc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nelson.valuation.exception.CalculationException;
import org.nelson.valuation.model.MarketData;
import org.nelson.valuation.model.ValuationCommand;
import org.nelson.valuation.model.ValuationModel;

import static org.junit.jupiter.api.Assertions.*;

class CalculationFunctionSelectorTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void selectGivenBsmModel() throws CalculationException, InterruptedException {
        CalculationFunction<ValuationCommand, Double> func = CalculationFunctionSelector.select(ValuationModel.BSM_MODEL);
        double result = func.apply( nonZeroSpotPriceCommand() );
        assertEquals(3, result);
    }

    @Test
    void selectGivenMonteCarloModel() throws CalculationException, InterruptedException {
        CalculationFunction<ValuationCommand, Double> func = CalculationFunctionSelector.select(ValuationModel.MC_MODEL);
        double result = func.apply( nonZeroSpotPriceCommand() );
        assertEquals(4, result);
    }

    @Test
    void selectGivenZeroPriceForMonteCarlo() throws CalculationException, InterruptedException {
        CalculationFunction<ValuationCommand, Double> func = CalculationFunctionSelector.select(ValuationModel.MC_MODEL);
        assertThrows(CalculationException.class, () -> {
            double result = func.apply( zeroSpotPriceCommand() );
        });
    }

    ValuationCommand nonZeroSpotPriceCommand() {
        ValuationCommand command = new ValuationCommand();
        MarketData marketData = new MarketData();
        marketData.setUnderlyingSpotPrice(2);
        command.setMarketData(marketData);
        return command;
    }

    ValuationCommand zeroSpotPriceCommand() {
        ValuationCommand command = new ValuationCommand();
        MarketData marketData = new MarketData();
        marketData.setUnderlyingSpotPrice(0);
        command.setMarketData(marketData);
        return command;
    }

}