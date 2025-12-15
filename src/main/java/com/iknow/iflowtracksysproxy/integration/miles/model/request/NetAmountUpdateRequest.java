package com.iknow.iflowtracksysproxy.integration.miles.model.request;

import lombok.Data;

@Data
public class NetAmountUpdateRequest {
    private String vehicleOrderItemId;
    private String sroid;
    private String fieldId;
    private String refAmount;
    private String curAmount;
    private String currencyId;
}
