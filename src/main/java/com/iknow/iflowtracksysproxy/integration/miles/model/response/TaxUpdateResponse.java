package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

// Ana sınıf: <SofEnvelope> kök etiketini temsil eder
@Data
@JacksonXmlRootElement(localName = "SofEnvelope")
public class TaxUpdateResponse {

    // Maps to the <data> element
    @JacksonXmlProperty(localName = "data")
    private ResponseData data;

    // Maps to the <metadata> element
    @JacksonXmlProperty(localName = "metadata")
    private ResponseMetadata metadata;

    // -------------------------------------------------------------------------
    // ---------- RESPONSE DATA (data) ----------
    // -------------------------------------------------------------------------
    @Data
    public static class ResponseData {
        // Maps to <MWSBulkAttributeUpdate>
        @JacksonXmlProperty(localName = "MWSBulkAttributeUpdate")
        private MWSBulkAttributeUpdate mwsBulkAttributeUpdate;
    }

    // ---------- MWSBulkAttributeUpdate ----------
    @Data
    public static class MWSBulkAttributeUpdate {
        // Maps to <MWSObject>
        @JacksonXmlProperty(localName = "MWSObject")
        private MWSObject mwsObject;
    }

    // ---------- MWSObject ----------
    @Data
    public static class MWSObject {
        // <id>1272137</id>
        @JacksonXmlProperty(localName = "id")
        private String id;

        // <sroid>210</sroid>
        @JacksonXmlProperty(localName = "sroid")
        private String sroid;

        // Maps to <Field_Set>
        @JacksonXmlProperty(localName = "Field_Set")
        private FieldSet fieldSet;
    }

    // ---------- FieldSet ----------
    // <Field_Set> etiketi içinde <Field> barındırır
    @Data
    public static class FieldSet {
        // Maps to <Field>
        @JacksonXmlProperty(localName = "Field")
        private Field field;
    }

    // ---------- Field ----------
    // <Field> etiketi
    @Data
    public static class Field {
        // <id>1038</id>
        @JacksonXmlProperty(localName = "id")
        private String id;

        // <name>cc_tax</name>
        @JacksonXmlProperty(localName = "name")
        private String name;

        // Maps to <MWSMCurrencyValue>
        @JacksonXmlProperty(localName = "MWSMCurrencyValue")
        private MWSMCurrencyValue mwsmCurrencyValue;
    }

    // ---------- MWSMCurrencyValue ----------
    // Para birimi değerini tutar
    @Data
    public static class MWSMCurrencyValue {
        // <refAmount>22250.69</refAmount>
        @JacksonXmlProperty(localName = "refAmount")
        private String refAmount;

        // <curAmount>22250.69</curAmount>
        @JacksonXmlProperty(localName = "curAmount")
        private String curAmount;

        // <currencyID>350001</currencyID>
        @JacksonXmlProperty(localName = "currencyID")
        private String currencyId;
    }

    // -------------------------------------------------------------------------
    // ---------- RESPONSE METADATA (metadata) ----------
    // -------------------------------------------------------------------------
    // <metadata> etiketini ve onun niteliklerini (attributes) temsil eder
    @Data
    public static class ResponseMetadata {

        // Maps to the <operationstatus> element INSIDE <metadata>
        // <metadata> içinde <operationstatus> etiketini bekler.
        @JacksonXmlProperty(localName = "operationstatus")
        private OperationStatus operationStatus;
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