package com.iknow.iflowtracksysproxy.integration.miles.model.request;

import lombok.Data;

@Data
public class SevkBaslangicTarihiUpdateRequest {
    private String deliveryConditionId;
    private String sroid;
    private String fieldId;
    private String dateTimeValue;
}
