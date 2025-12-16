package com.iknow.iflowtracksysproxy.integration.miles.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxUpdateRequest {
    private String vehicleOrderItemId;
    private String orderId;
    private String fieldId;
    private String refAmount;
    private String curAmount;
    private String currencyId;
}
