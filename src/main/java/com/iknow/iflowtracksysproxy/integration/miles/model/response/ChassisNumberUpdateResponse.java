package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "SofEnvelope")
public class ChassisNumberUpdateResponse {

    @JacksonXmlProperty(localName = "data")
    private ResponseData responseData;

    @JacksonXmlProperty(localName = "metadata")
    private ResponseMetadata responsemetadata;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ResponseData {
        @JacksonXmlProperty(localName = "MWSBulkAttributeUpdate")
        private MWSBulkAttributeUpdate mwsBulkAttributeUpdate;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MWSBulkAttributeUpdate {
        @JacksonXmlProperty(localName = "MWSObject")
        private MWSObject mwsObject;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MWSObject {
        @JacksonXmlProperty(localName = "id")
        private String id;

        @JacksonXmlProperty(localName = "sroid")
        private String sroid;

        @JacksonXmlProperty(localName = "Field_Set")
        private FieldSet fieldset;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FieldSet {
        @JacksonXmlProperty(localName = "Field")
        private Field field;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Field {
        @JacksonXmlProperty(localName = "id")
        private String id;

        @JacksonXmlProperty(localName = "name")
        private String name;

        @JacksonXmlProperty(localName = "MWSStringValue")
        private MWSStringValue mwsStringValue;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MWSStringValue {
        @JacksonXmlProperty(localName = "value")
        private String value;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ResponseMetadata {
        @JacksonXmlProperty(localName = "operationstatus")
        private OperationStatus operationStatus;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OperationStatus {

        @JacksonXmlProperty(isAttribute = true)
        @JsonAlias("businesserror")
        private String businessError;

        @JacksonXmlProperty(isAttribute = true)
        @JsonAlias("technicalerror")
        private String technicalError;

        @JacksonXmlProperty(isAttribute = true)
        @JsonAlias("calculationerror")
        private String calculationError;
    }
}
