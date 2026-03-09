package com.iknow.iflowtracksysproxy.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DealerInvoiceMailRequest {
    private String ordersId;
    private String vehicleDescription;
    private String customerTradingName;
    private String supplierTradingName;
    private String color;
    private String deliveryLocation;
}
