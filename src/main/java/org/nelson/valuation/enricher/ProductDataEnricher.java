package org.nelson.valuation.enricher;

import org.nelson.valuation.model.ProductData;
import org.nelson.valuation.model.ProductType;
import org.nelson.valuation.model.ValuationCommand;
import org.nelson.valuation.model.ValuationModel;

public class ProductDataEnricher {

    private ProductDataEnricher() {
    }

    public static ProductData enrich(ValuationCommand command) {
        ProductData productData = new ProductData();
        if (command.getValuationRequest().getProductType() == ProductType.VANILLA) {
            productData.setValuationModel(ValuationModel.BSM_MODEL);
        }
        else {
            productData.setValuationModel(ValuationModel.MC_MODEL);
        }
        return productData;
    }

}
