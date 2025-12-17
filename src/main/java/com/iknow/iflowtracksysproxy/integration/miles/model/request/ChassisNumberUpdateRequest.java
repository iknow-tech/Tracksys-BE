package com.iknow.iflowtracksysproxy.integration.miles.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChassisNumberUpdateRequest {
    private String orderId;
    private String fieldId;
    private String value;

}
