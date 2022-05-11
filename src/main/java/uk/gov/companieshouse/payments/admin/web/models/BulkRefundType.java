package uk.gov.companieshouse.payments.admin.web.models;

import javax.validation.constraints.NotNull;

public class BulkRefundType {

    @NotNull(message = "Please choose a payment method")
    private String selectedBulkRefundType;

    public String getSelectedBulkRefundType() {
        return selectedBulkRefundType;
    }

    public void setSelectedBulkRefundType(String selectedBulkRefundType) {
        this.selectedBulkRefundType = selectedBulkRefundType;
    }
}
