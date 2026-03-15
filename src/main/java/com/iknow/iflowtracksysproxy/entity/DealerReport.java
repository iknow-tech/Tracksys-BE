package com.iknow.iflowtracksysproxy.entity;

import com.iknow.iflowtracksysproxy.entity.base.ReportBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "report_dealer")
@Getter
@Setter
public class DealerReport extends ReportBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shipment_city_contract")
    private String shipmentCityContract;

    @Column(name = "vehicle_delivery_service")
    private String vehicleDeliveryService;

    @Column(name = "proforma_total")
    private BigDecimal proformaTotal;

    @Column(name = "shipment_start_date")
    private LocalDateTime shipmentStartDate;

    @Column(name = "shipment_end_date")
    private LocalDateTime shipmentEndDate;
}