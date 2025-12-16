package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "SofEnvelope")
public class ChassisNumberUpdateResponse {

    @JacksonXmlProperty(localName = "data")
    private ResponseData responseData;

    @JacksonXmlProperty(localName = "metadata")
    private ResponseMetadata responsemetadata;

    @Data
    public static class ResponseData{
        @JacksonXmlProperty(localName = "MWSBulkAttributeUpdate")
        private MWSBulkAttributeUpdate mwsBulkAttributeUpdate;
    }

    @Data
    public static class MWSBulkAttributeUpdate{
        @JacksonXmlProperty(localName = "MWSObject")
        private MWSObject mwsObject;
    }

    @Data
    public static class MWSObject{
        @JacksonXmlProperty(localName = "id")
        private String id;

        @JacksonXmlProperty(localName = "sroid")
        private String sroid;

        @JacksonXmlProperty(localName = "Field_Set")
        private FieldSet fieldset;
    }

    @Data
    public static class FieldSet{
        @JacksonXmlProperty(localName = "Field")
        private Field field;
    }

    @Data
    public static class Field{
        @JacksonXmlProperty(localName = "id")
        private String id;

        @JacksonXmlProperty(localName = "name")
        private String name;

        @JacksonXmlProperty(localName = "MWSStringValue")
        private MWSStringValue mwsStringValue;
    }

    @Data
    public static class MWSStringValue{
        @JacksonXmlProperty(localName = "value")
        private String value;
    }

    @Data
    public static class ResponseMetadata {
        @JacksonXmlProperty(localName = "operationstatus")
        private OperationStatus operationStatus;
    }

    @Data
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
