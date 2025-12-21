package com.iknow.iflowtracksysproxy.integration.miles.model.request;

import lombok.Data;

@Data
public class PlakaAvadanlikAlindiTarihiUpdateRequest {
    private String vehiclePropertyId; // Vehicle Property Id (licenseplateandequipmentrequestdate'den gelen)
    private String sroid; // 262 - sabit Order Objesi
    private String fieldId; // 2396 - sabit Alan id
    private String dateTimeValue; // Plaka ve Avadanlık Sevki Alındı Tarihi (ISO 8601 formatında)
}
