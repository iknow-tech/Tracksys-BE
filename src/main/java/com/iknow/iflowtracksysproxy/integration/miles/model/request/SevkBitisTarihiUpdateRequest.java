package com.iknow.iflowtracksysproxy.integration.miles.model.request;

import lombok.Data;

@Data
public class SevkBitisTarihiUpdateRequest {
    private String deliveryConditionId; // DeliveryCondition Id (PRJ_SM_CustomerContract veya
                                        // PRJ_SM_StockVehicleContract'tan gelen)
    private String sroid; // 264 - sabit Order Objesi
    private String fieldId; // 1000014 - sabit Alan id
    private String dateTimeValue; // Sevk Bitiş Tarihi (ISO 8601 formatında)
}
