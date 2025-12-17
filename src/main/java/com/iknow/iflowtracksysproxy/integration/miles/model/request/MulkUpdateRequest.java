package com.iknow.iflowtracksysproxy.integration.miles.model.request;

import lombok.Data;

@Data
public class MulkUpdateRequest {
    private String fleetVehicleId;
    private String sroid; // 68 - sabit Order Objesi
    private String fieldId; // 1001733 - sabit Alan id
    private String value; // 1006514 - sabit Finansal Kiralama
}
