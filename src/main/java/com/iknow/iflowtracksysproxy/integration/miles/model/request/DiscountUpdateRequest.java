package com.iknow.iflowtracksysproxy.integration.miles.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiscountUpdateRequest {
    private String orderId;
    private String fieldId;
    private String refAmount;
    private String curAmount;
    private String currencyId;
}
