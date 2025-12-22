package com.iknow.iflowtracksysproxy.integration.miles.model.request;

import lombok.Data;

@Data
public class TrafficRegistrationNumberUpdaterequest {
    private String vehiclePropertyId;
    private String fieldId;
    private String dateTime;
    private String orderId;
}
