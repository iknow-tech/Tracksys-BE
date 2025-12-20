package com.iknow.iflowtracksysproxy.integration.miles.model.request;


import lombok.Data;

@Data
public class HgsTalepTarihiUpdateRequest {
    private String vehiclePropertyId;
    private String sroid;
    private String fieldId;
    private String dateTimeValue;
}