package org.nelson.valuation.model;

import org.nelson.valuation.calc.CalculationFunction;

public class ValuationCommand {

    private boolean isFastCalculation;

    private ValuationRequest valuationRequest;

    private ProductData productData;
    private MarketData marketData;

    private CalculationFunction<ValuationCommand, Double> calculation;

    public ValuationCommand() {
    }

    public boolean isFastCalculation() {
        return isFastCalculation;
    }

    public void setFastCalculation(boolean fastCalculation) {
        isFastCalculation = fastCalculation;
    }

    public ValuationRequest getValuationRequest() {
        return valuationRequest;
    }

    public void setValuationRequest(ValuationRequest valuationRequest) {
        this.valuationRequest = valuationRequest;
    }

    public ProductData getProductData() {
        return productData;
    }

    public void setProductData(ProductData productData) {
        this.productData = productData;
    }

    public MarketData getMarketData() {
        return marketData;
    }

    public void setMarketData(MarketData marketData) {
        this.marketData = marketData;
    }

    public CalculationFunction<ValuationCommand, Double> getCalculation() {
        return calculation;
    }

    public void setCalculation(CalculationFunction<ValuationCommand, Double> calculation) {
        this.calculation = calculation;
    }

}
