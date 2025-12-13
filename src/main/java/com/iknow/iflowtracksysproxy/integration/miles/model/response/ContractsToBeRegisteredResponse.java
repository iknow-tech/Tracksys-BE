package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

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
    @JsonAlias("orders_id")
    private String ordersId;
    @JsonAlias("vehicleorder_id")
    private String vehicleOrderId;
    @JsonAlias("netprice")
    private String netPrice;
}
