package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "SofEnvelope")
public class NetAmountUpdateResponse {

    @JsonProperty("data")
    private DataWrapper data;

    @JsonProperty("metadata")
    private MetadataWrapper metadata;

    @Data
    public static class DataWrapper {
        @JsonProperty("MWSBulkAttributeUpdate")
        private MWSBulkAttributeUpdate mwsBulkAttributeUpdate;
    }

    @Data
    public static class MWSBulkAttributeUpdate {
        @JsonProperty("MWSObject")
        private MWSObject mwsObject;
    }

    @Data
    public static class MWSObject {
        @JsonProperty("id")
        private String id;

        @JsonProperty("sroid")
        private String sroid;

        @JsonProperty("Field_Set")
        private FieldSet fieldSet;
    }

    @Data
    public static class FieldSet {
        @JsonProperty("Field")
        private Field field;
    }

    @Data
    public static class Field {
        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("MWSMCurrencyValue")
        private MWSMCurrencyValue mwsmCurrencyValue;
    }

    @Data
    public static class MWSMCurrencyValue {
        @JsonProperty("refAmount")
        private String refAmount;

        @JsonProperty("curAmount")
        private String curAmount;

        @JsonProperty("currencyId")
        private String currencyId;
    }

    @Data
    public static class MetadataWrapper {
        @JsonProperty("operationstatus")
        private OperationStatus operationstatus;
    }

    @Data
    public static class OperationStatus {
        @JacksonXmlProperty(isAttribute = true)
        private String businesserror;

        @JacksonXmlProperty(isAttribute = true)
        private String technicalerror;

        @JacksonXmlProperty(isAttribute = true)
        private String calculationerror;
    }
}
