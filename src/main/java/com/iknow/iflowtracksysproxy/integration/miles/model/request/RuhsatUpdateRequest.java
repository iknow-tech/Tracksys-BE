package com.iknow.iflowtracksysproxy.integration.miles.model.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RuhsatUpdateRequest {
    private String vehiclePropertyId; // Vehicle Property Id
    private String sroid; // 262 - sabit Order Objesi
    private String fieldId; // 1326 - sabit Alan id
    private String ruhsatBelgeNo; // Ruhsat Belge No değeri
    private LocalDateTime expirationDate;
}
