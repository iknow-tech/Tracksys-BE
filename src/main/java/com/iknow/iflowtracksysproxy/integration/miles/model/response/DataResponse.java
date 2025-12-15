package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

@Data
public class DataResponse {
    @JsonProperty("SofToken")
    private LogonResponse loginResponse;

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "PRJ_SM_CustomerContract_Set")
    @JacksonXmlProperty(localName = "PRJ_SM_CustomerContract")
    private List<CustomerContractResponse> customerContracts;

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "PRJ_SM_StockVehicleContract_Set")
    @JacksonXmlProperty(localName = "PRJ_SM_StockVehicleContract")
    private List<StockVehicleContractResponse> stockVehicleContracts;

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "PRJ_SM_ContractsToBeRegistered_Set")
    @JacksonXmlProperty(localName = "PRJ_SM_ContractsToBeRegistered")
    private List<ContractsToBeRegisteredResponse> contractsToBeRegistered;

}
