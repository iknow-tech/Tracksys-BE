package com.iknow.iflowtracksysproxy.integration.miles.model.request;

import lombok.Data;

@Data
public class TrafikSigortasiTalepTarihiUpdateRequest {
    private String vehiclePropertyId; // Vehicle Property Id (trafficinsurance'dan gelen)
    private String sroid; // 262 - sabit Order Objesi
    private String fieldId; // 2397 - sabit Alan id
    private String dateTimeValue; // Trafik Sigortası Talep Tarihi (ISO 8601 formatında)
}
