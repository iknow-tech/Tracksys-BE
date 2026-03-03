package com.iknow.iflowtracksysproxy.integration.miles.model.request;

import lombok.Data;

@Data
public class TriggerMWSBulkProcessor_ApproveContractRequest {
    private String contractId;
    private String deliveryDate;
}
