package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "SofEnvelope")
public class PropertyTypeUpdateResponse {


    @JacksonXmlProperty(localName = "data")
    private ResponseData data;

    @JacksonXmlProperty(localName = "metadata")
    private ResponseMetadata metadata;

    @Data
    public static class ResponseData {
        @JacksonXmlProperty(localName = "MWSBulkAttributeUpdate")
        private MWSBulkAttributeUpdate mwsBulkAttributeupdate;
    }

    @Data
    public static class MWSBulkAttributeUpdate {
        @JacksonXmlProperty(localName = "MWSObject")
        private MWSObject mwsObject;
    }

    @Data
    public static class MWSObject {
        @JacksonXmlProperty(localName = "id")
        private String id;

        @JacksonXmlProperty(localName = "sroid")
        private String sroid;

        @JacksonXmlProperty(localName = "Field_Set")
        private FieldSet fieldSet;
    }

    @Data
    public static class FieldSet {
        @JacksonXmlProperty(localName = "Field")
        private Field field;
    }

    @Data
    public static class Field {
        @JacksonXmlProperty(localName = "id")
        private String id;

        @JacksonXmlProperty(localName = "name")
        private String name;

        @JacksonXmlProperty(localName = "MWSEnumerationValue")
        private MWSEnumarationValue mwsEnumerationValue;
    }

    @Data
    public static class MWSEnumarationValue {
        @JacksonXmlProperty(localName = "value")
        private String value;
    }

    @Data
    public static class ResponseMetadata {

        // Maps to the <operationstatus> element INSIDE <metadata>
        // <metadata> içinde <operationstatus> etiketini bekler.
        @JacksonXmlProperty(localName = "operationstatus")
        private TaxUpdateResponse.OperationStatus operationStatus;
    }

    // Yeni OperationStatus sınıfı: operationstatus etiketini ve niteliklerini temsil eder
    @Data
    public static class OperationStatus {

        // operationstatus businesserror="false" (Nitelikleri yakalar)
        @JacksonXmlProperty(isAttribute = true)
        @JsonAlias("businesserror")
        private String businessError;

        // operationstatus technicalerror="false"
        @JacksonXmlProperty(isAttribute = true)
        @JsonAlias("technicalerror")
        private String technicalError;

        // operationstatus calculationerror="false"
        @JacksonXmlProperty(isAttribute = true)
        @JsonAlias("calculationerror")
        private String calculationError;
    }
}
