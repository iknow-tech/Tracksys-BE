package com.iknow.iflowtracksysproxy.integration.miles.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNetAmountRequest {
    String vehicleOrderItemId;
    String refAmount;
    String curAmount;
}
