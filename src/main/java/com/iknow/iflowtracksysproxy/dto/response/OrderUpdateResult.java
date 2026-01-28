package com.iknow.iflowtracksysproxy.dto.response;

import lombok.Data;

@Data
public class OrderUpdateResult {
    private String contractId;
    private Long ordersId;

    private Long supplierId;
    private Long contactId;

    private boolean success;
}
