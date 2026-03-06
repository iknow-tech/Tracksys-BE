package com.iknow.iflowtracksysproxy.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class VehicleDocumentUpdateItemRequest {
    private String contractId;
    private String fleetVehicleId;
    private String licensePlate;
    private String licenseSerialNumber;
    private LocalDate expirationDate;
    private String hgsCode;
    private LocalDate hgsRequestedDate;
    private LocalDate licensePlateEquipmentRequestDate;
    private LocalDate licensePlateEquipmentTransferDate;
    private LocalDate trafficInsuranceDate;
    private LocalDate registNoRequestDate;

}