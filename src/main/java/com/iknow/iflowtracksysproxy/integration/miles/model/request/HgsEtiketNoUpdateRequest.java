package com.iknow.iflowtracksysproxy.integration.miles.model.request;

import lombok.Data;

@Data
public class HgsEtiketNoUpdateRequest {
    private String vehiclePropertyId;
    private String sroid;
    private String fieldId;
    private String hgsEtiketNo;
}
