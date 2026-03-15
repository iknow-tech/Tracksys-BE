package com.iknow.iflowtracksysproxy.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class ReportBase {

    @Column(name = "contract_id")
    private String contractId;

    @Column(name = "license_plate")
    private String licensePlate;

    @Column(name = "chassis_number")
    private String chassisNumber;

    @Column(name = "engine_number")
    private String engineNumber;

    @Column(name = "color")
    private String color;

    @Column(name = "dealer")
    private String dealer;

    @Column(name = "delivery_dealer")
    private String deliveryDealer;

    @Column(name = "vehicle_description")
    private String vehicleDescription;

    @Column(name = "customer")
    private String customer;

    @Column(name = "status")
    private String status;

    @Column(name = "license_plate_equipment_request_date")
    private LocalDateTime licensePlateEquipmentRequestDate;

    @Column(name = "license_plate_equipment_shipment_date")
    private LocalDateTime licensePlateEquipmentShipmentDate;
}