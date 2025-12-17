package com.iknow.iflowtracksysproxy.integration.miles.model.request;

import lombok.Data;

@Data
public class SasiNoUpdateRequest {
    private String fleetVehicleId;
    private String sroid; // 68 - sabit Order Objesi
    private String fieldId; // 917 - sabit Alan id
    private String sasiNo; // Şasi numarası değeri
}
