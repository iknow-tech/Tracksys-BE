package com.iknow.iflowtracksysproxy.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDocumentAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contract_id", nullable = false, length = 50)
    private String contractId;

    @Column(name = "license_plate", length = 50)
    private String licensePlate;

    @Column(name = "license_serial_no", length = 50)
    private String licenseSerialNumber;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "hgs_no")
    private String hgsTagNo;

    @Column(name = "hgs_date")
    private LocalDate hgsRequestedDate;

    @Column(name = "license_plate_equipment_date")
    private LocalDate licensePlateEquipmentRequestDate;

    @Column(name = "license_plate_equipment_transfer_date")
    private LocalDate licensePlateEquipmentTransferDate;

    @Column(name = "traffic_insurance_date")
    private LocalDate trafficInsuranceDate;

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;



}
