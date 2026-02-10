package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "SofEnvelope")
public class VehicleOrderDescUpdateResponse {

    @JsonProperty("data")
    private DataWrapper data;

    @JsonProperty("metadata")
    private MetadataWrapper metadata;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DataWrapper {
        @JsonProperty("MWSBulkAttributeUpdate")
        private MWSBulkAttributeUpdate mwsBulkAttributeUpdate;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MWSBulkAttributeUpdate {
        @JsonProperty("MWSObject")
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
        private FieldSet fieldSet;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FieldSet {
        @JsonProperty("Field")
        private Field field;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Field {
        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("MWSMStringValue")
        private MWSMStringValue mwsmStringValue;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MWSMStringValue {
        @JsonProperty("value")
        private String value;

    }

    @Data
    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MetadataWrapper {
        @JsonProperty("operationstatus")
        private OperationStatus operationstatus;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OperationStatus {
        @JacksonXmlProperty(isAttribute = true)
        private String businesserror;

        @JacksonXmlProperty(isAttribute = true)
        private String technicalerror;

        @JacksonXmlProperty(isAttribute = true)
        private String calculationerror;
    }
}
