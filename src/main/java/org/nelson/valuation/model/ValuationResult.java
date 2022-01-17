package org.nelson.valuation.model;

public class ValuationResult {

    private int requestId;
    private String productId;
    private ProductType productType;

    private ValuationResultStatus status;
    private String errorMessage;
    private double result;

    public ValuationResult() {
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public ValuationResultStatus getStatus() {
        return status;
    }

    public void setStatus(ValuationResultStatus status) {
        this.status = status;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    @Override
    public String toString() {
        return "ValuationResult{" +
                "requestId=" + requestId +
                ", productId='" + productId + '\'' +
                ", productType=" + productType +
                ", status=" + status +
                ", errorMessage='" + errorMessage + '\'' +
                ", result=" + result +
                '}';
    }

}
