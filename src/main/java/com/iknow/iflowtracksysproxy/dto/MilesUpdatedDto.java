package com.iknow.iflowtracksysproxy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MilesUpdatedDto {
    private String contractId;
    private String netPrice;
    private String otv;
    private String chassisNumber;
    private String motorNumber;
    private String ettn;
    private LocalDate shipmentStartDate;
    private LocalDate shipmentEndDate;
    private String delivery;
    private Boolean creditApprovalCheck= false;
    private String deliveryConditionId;

    // ----------------------vehicle-document--------------------------------
    private String licensePlate;
    private String licenseSerialNumber;
    private String fleetVehicleId;
    private LocalDate expirationDate;
    private String hgsCode;
    private LocalDate hgsRequestedDate;
    private LocalDate licensePlateEquipmentRequestDate;
    private LocalDate licensePlateEquipmentTransferDate;
    private LocalDate trafficInsuranceDate;








}
