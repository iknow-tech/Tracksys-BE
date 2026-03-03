package com.iknow.iflowtracksysproxy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealerContractInfo {
    private String id;
    private String contractId;
    private String contractApprovedDate;
    private String customer;
    private String make;
    private String model;
    private String modelYear;
    private String version;
    private String color;
    private String deliveryPerson;
    private String recipientPerson;
    private String ordersId;
    private String options;
    private String uttsGpsInstallation;
    private String treasuryApprovalDate;
    private String ettn;
    private BigDecimal netPrice;
    private BigDecimal otv;
    private String chassisNumber;
    private String motorNumber;
    private String shipmentStartDate;
    private String shipmentEndDate;
    private String deliveryDate;
    private String dealerName;
    private LocalDateTime assignedDate;
    private String leasingName;
    private LocalDate leasingInvoiceDate;
    private String sysEnumerationId;
    private String assignedBy;
    private String status;
    private String deliverySupplier;
    private String deliveryLocation;
    private boolean hasProforma;
    private String deliveryTerms;
    private String deliveryMethod;
    private String deliveryDocumentId;
    private String deliveryDocumentName;
}
