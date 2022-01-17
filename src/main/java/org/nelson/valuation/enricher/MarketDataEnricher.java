package org.nelson.valuation.enricher;

import org.nelson.valuation.model.MarketData;
import org.nelson.valuation.model.ValuationCommand;

import java.util.Random;

public class MarketDataEnricher {

    private static Random random = new Random();

    private MarketDataEnricher() {
    }

    public static MarketData enrich(ValuationCommand command) {
        MarketData marketData = new MarketData();
        marketData.setUnderlyingSpotPrice(random.nextInt(20));
        return marketData;
    }

}
