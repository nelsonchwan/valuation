package org.nelson.valuation.model;

public class ValuationRequest {

    private int requestId;
    private String productId;
    private ProductType productType;


    public ValuationRequest(
            int requestId,
            String productId,
            ProductType productType
    ) {
        this.requestId = requestId;
        this.productId = productId;
        this.productType = productType;
    }

    public int getRequestId() {
        return requestId;
    }

    public String getProductId() {
        return productId;
    }

    public ProductType getProductType() {
        return productType;
    }

}
