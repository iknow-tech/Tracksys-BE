package com.iknow.iflowtracksysproxy.integration.miles.model.request;


import lombok.Data;

@Data
public class DeliveryDealerAreaUpdateRequest {
    private String contractId;
    private String orderId;
    private String fieldId;
    private String value;
}
