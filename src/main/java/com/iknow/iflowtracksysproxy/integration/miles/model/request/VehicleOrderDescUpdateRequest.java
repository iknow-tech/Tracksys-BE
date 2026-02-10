package com.iknow.iflowtracksysproxy.integration.miles.model.request;

import lombok.Data;

@Data
public class VehicleOrderDescUpdateRequest {
    private String vehicleOrderId;
    private String sroid;
    private String fieldId;
    private String value;
}
