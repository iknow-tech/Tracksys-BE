package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    @JsonProperty
    @JacksonXmlElementWrapper(localName = "PRJ_SM_VehicleDocuments_Set")
    @JacksonXmlProperty(localName = "PRJ_SM_VehicleDocuments")
    private List<VehicleDocumentsResponse> vehicleDocuments;

    @JsonProperty("MWSBulkAttributeUpdate")
    @JacksonXmlProperty(localName = "MWSBulkAttributeUpdate")
    private MWSBulkAttributeUpdate mwsBulkAttributeUpdate;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MWSBulkAttributeUpdate {
        @JsonProperty("MWSObject")
        @JacksonXmlProperty(localName = "MWSObject")
        private MWSObject mwsObject;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MWSObject {
        @JsonProperty("id")
        private String id;

        @JsonProperty("sroid")
        private String sroid;

        @JsonProperty("Field_Set")
        @JacksonXmlProperty(localName = "Field_Set")
        private FieldSet fieldSet;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FieldSet {
        @JsonProperty("Field")
        @JacksonXmlProperty(localName = "Field")
        private Field field;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Field {
        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("MWSStringValue")
        @JacksonXmlProperty(localName = "MWSStringValue")
        private MWSStringValue mwsStringValue;

        @JsonProperty("MWSEnumerationValue")
        @JacksonXmlProperty(localName = "MWSEnumerationValue")
        private MWSEnumerationValue mwsEnumerationValue;

        @JsonProperty("MWSMCurrencyValue")
        @JacksonXmlProperty(localName = "MWSMCurrencyValue")
        private MWSMCurrencyValue mwsmCurrencyValue;

        @JsonProperty("MWSDateTimeValue")
        @JacksonXmlProperty(localName = "MWSDateTimeValue")
        private MWSDateTimeValue mwsDateTimeValue;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MWSStringValue {
        @JsonProperty("value")
        private String value;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MWSEnumerationValue {
        @JsonProperty("value")
        private String value;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MWSMCurrencyValue {
        @JsonProperty("refAmount")
        private String refAmount;

        @JsonProperty("curAmount")
        private String curAmount;

        @JsonProperty("currencyId")
        private String currencyId;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MWSDateTimeValue {
        @JsonProperty("value")
        private String value;
    }
}
