package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.iknow.iflowtracksysproxy.entity.ContractOrderStatus;
import com.iknow.iflowtracksysproxy.entity.ContractStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CustomerContractResponse {
    @JsonAlias("id ")
    private String id ;
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
    @JsonAlias("vehicleorderitem_id")
    private String vehicleOrderItemId;
    @JsonAlias("netprice")
    private String netPrice;
    @JsonAlias("deliveryperson")
    private String deliveryPerson;
    @JsonAlias("deliveryterms")
    private String deliveryTerms;
    @JsonAlias("deliverysupplier")
    private String deliverySupplier;
    @JsonAlias("discountedprice")
    private String discountedPrice;
    @JsonAlias("motornumber")
    private String motorNumber;
    @JsonAlias("options")
    private String options;
    @JsonAlias("contractapproveddate")
    private String contractapproveddate;
    @JsonAlias("uttsgpsinstallation")
    private String uttsGpsInstallation;
    @JsonAlias("treasuryapprovaldate")
    private String treasuryApprovalDate;
    @JsonAlias("deliverydate")
    private String deliveryDate;
    @JsonAlias("deliverycondition_id")
    private String deliveryConditionId;


    // assigned dealer
    private String assignedDealer;

    // assigned leasing
    private String assignedLeasing;
    private String sysEnumerationId;

    //proforma
    private boolean hasProforma;
    private LocalDate leasingInvoiceDate;
    private String deliveryMethod;

    private ContractMilesUpdateResponse contractMilesUpdate;

    private boolean updateVehicleOrderDesc;
    private boolean updateVehicleOrderItemStatu;
    private String deliveryDocumentId;
    private String deliveryDocumentName;
    private ContractStatus status = ContractStatus.ACTIVE;

    // sipariş teslim mi edildi?
    private String deliveredBy;
    private LocalDateTime orderDeliveredDate;

    // sipariş iptal mi edildi?
    private String canceledBy;
    private LocalDateTime canceledDate;

    // fleet vehicle üzerinde mülkiyet türü - ak leasing
    private boolean mulkiyetUpdateSuccess;

    // fleet vehicle üzerinde mülk alanı update
    private boolean mulkUpdateSuccess;

    private ContractOrderStatus contractOrderStatus;




}
