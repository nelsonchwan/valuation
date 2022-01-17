package org.nelson.valuation.engine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nelson.valuation.calc.CalculationFunctionSelector;
import org.nelson.valuation.model.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ValuationEngineTest {

    ValuationEngine valuationEngine;
    BlockingQueue<ValuationCommand> calculationQueue;
    BlockingQueue<ValuationResult> resultQueue;

    @BeforeEach
    void setUp() {
        calculationQueue = new LinkedBlockingQueue<>(10);
        resultQueue = new LinkedBlockingQueue<>(10);
        valuationEngine = new ValuationEngine("Engine1", calculationQueue, resultQueue);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void runUponCommandReceived() throws InterruptedException {
        calculationQueue.offer( command() );
        Thread engineThread = new Thread(valuationEngine);
        engineThread.start();
        //ValuationResult result = resultQueue.take();
        ValuationResult result = resultQueue.poll(1, TimeUnit.MINUTES);
        engineThread.interrupt();
        assertNotNull(result);
        assertEquals(3, result.getResult());
        Assertions.assertEquals(ValuationResultStatus.SUCCESS, result.getStatus());
    }

    @Test
    void runUponZeroPriceCommandReceived() throws InterruptedException {
        calculationQueue.offer( zeroPriceCommand() );
        Thread engineThread = new Thread(valuationEngine);
        engineThread.start();
        //ValuationResult result = resultQueue.take();
        ValuationResult result = resultQueue.poll(1, TimeUnit.MINUTES);
        engineThread.interrupt();
        assertNotNull(result);
        assertEquals(ValuationResultStatus.FAILED, result.getStatus());
        assertEquals("Underlying Spot Price is 0!", result.getErrorMessage());
    }

    ValuationCommand command() {
        ValuationCommand command = new ValuationCommand();
        ValuationRequest request = new ValuationRequest(1, "V1", ProductType.VANILLA);
        command.setValuationRequest(request);
        MarketData marketData = new MarketData();
        marketData.setUnderlyingSpotPrice(2);
        command.setMarketData(marketData);
        command.setCalculation( CalculationFunctionSelector.select(ValuationModel.BSM_MODEL) );
        return command;
    }

    ValuationCommand zeroPriceCommand() {
        ValuationCommand command = new ValuationCommand();
        ValuationRequest request = new ValuationRequest(1, "V1", ProductType.VANILLA);
        command.setValuationRequest(request);
        MarketData marketData = new MarketData();
        marketData.setUnderlyingSpotPrice(0);
        command.setMarketData(marketData);
        command.setCalculation( CalculationFunctionSelector.select(ValuationModel.BSM_MODEL) );
        return command;
    }

}