package com.iknow.iflowtracksysproxy.integration.miles.model.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VehicleInspectionDateUpdateRequest {
    private String vehiclePropertyId; // Vehicle Property Id (vehicleinspection'dan gelen)
    private String sroid; // 262 - sabit Order Objesi
    private String fieldId; // 2837 - sabit Alan id
    private String dateTimeValue; // Muayene geçerlilik sonu tarihi (ISO 8601 formatında)
}
