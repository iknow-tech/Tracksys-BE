package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ContractsToBeRegisteredResponse {
    @JsonAlias("contract_id")
    private String contractId;
    @JsonAlias("contractstate")
    private String contractState;
    @JsonAlias("contracttype")
    private String contractType;
    @JsonAlias("businesspartner_id")
    private String businessPartnerId;
    @JsonAlias("customervatnumber")
    private String customerVatNumber;
    @JsonAlias("customer")
    private String customer;
    @JsonAlias("py")
    private String py;
    @JsonAlias("fleetvehicle_id")
    private String fleetVehicleId;
    @JsonAlias("fleetvehiclestatus")
    private String fleetVehicleStatus;
    @JsonAlias("licenseplate")
    private String licensePlate;
    @JsonAlias("chassisnumber")
    private String chassisNumber;
    @JsonAlias("make")
    private String make;
    @JsonAlias("model")
    private String model;
    @JsonAlias("version")
    private String version;
    @JsonAlias("color")
    private String color;
    @JsonAlias("modelyear")
    private String modelYear;
    @JsonAlias("deliverylocation")
    private String deliveryLocation;
    @JsonAlias("tyrespecification")
    private String tyreSpecification;
    @JsonAlias("region")
    private String region;
    @JsonAlias("deliverysupplier")
    private String deliverySupplier;
    @JsonAlias("orders_id")
    private String ordersId;
    @JsonAlias("vehicleorder_id")
    private String vehicleOrderId;
    @JsonAlias("netprice")
    private String netPrice;
    @JsonAlias("logoservice")
    private String logoService;
    @JsonAlias("property")
    private String property; // mülk
    @JsonAlias("ownership")
    private String ownership; // mülkiyet
    @JsonAlias("licenseplateaccessories")
    private String licensePlateAccessories; // plaka avadanlık aksesuarları


    // satın alma birimin atadaığı bayi bilgisi
    private String assignedDealer;

    private String licenseSerialNumber;
    private LocalDate expirationDate;
    private String hgsCode;
    private LocalDate hgsDate;
    private LocalDate licensePlateEquipmentRequestDate;
    private LocalDate licensePlateEquipmentTransferDate;
    private LocalDate trafficInsuranceDate;
    private LocalDate hgsRequestedDate;
    private LocalDate registNoRequestDate;

}
