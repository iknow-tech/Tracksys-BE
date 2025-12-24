package com.iknow.iflowtracksysproxy.integration.miles.model.request;

import lombok.Data;

@Data
public class TriggerMWSBulkProcessor_ApproveContractRequest {
    private String contractId;
    private String deliveryDate;
    private String deliveryMileage;
    private String receiptByContact;
    private String isDriver;
    private String deliveryLocation;
}
