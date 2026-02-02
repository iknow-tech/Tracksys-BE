package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class VehicleOrderSupplierUpdateResponse {
    @JsonAlias("result")
    private String result;
}
