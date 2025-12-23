package com.iknow.iflowtracksysproxy.integration.miles.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalDateUpdateRequest {
    private String vehicleOrderItemId;
    private String orderId;
    private String fieldId;
    private String approvalDate;
}
