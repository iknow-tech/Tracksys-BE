package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

// Ana sınıf: <SofEnvelope> kök etiketini temsil eder
@Data
@JacksonXmlRootElement(localName = "SofEnvelope")
public class DiscountUpdateResponse {

    // Maps to the <data> element
    @JacksonXmlProperty(localName = "data")
    private ResponseData data;

    // Maps to the <metadata> element
    // NOT: Metadata artık alt eleman değil, sadece öznitelik taşıyan boş bir etiket.
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

    // ---------- Field (Görseldeki İndirim Alanına Uyarlanmıştır) ----------
    // <Field> etiketi
    @Data
    public static class Field {
        // <id>1040</id> (Görseldeki id: 1040)
        @JacksonXmlProperty(localName = "id")
        private String id;

        // <name>cc_discountAmount</name> (Görseldeki name: cc_discountAmount)
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
        // <refAmount>0.0</refAmount>
        @JacksonXmlProperty(localName = "refAmount")
        private String refAmount;

        // <curAmount>0.0</curAmount>
        @JacksonXmlProperty(localName = "curAmount")
        private String curAmount;

        // <currencyID>350001</currencyID>
        @JacksonXmlProperty(localName = "currencyId")
        private String currencyId;
    }

    // -------------------------------------------------------------------------
    // ---------- RESPONSE METADATA (metadata) (Görseldeki Formata Uyarlanmıştır)----------
    // -------------------------------------------------------------------------
    // <metadata> etiketini ve onun niteliklerini (attributes) temsil eder
    // Görselde: <metadata operationstatus="false" technicalerror="false" calculationerror="false"/>
    // Bu, <metadata> etiketinin kendisinin öznitelik taşıdığı ve alt elemanı olmadığı anlamına gelir.
    @Data
    public static class ResponseMetadata {

        @JacksonXmlProperty(localName = "operationstatus")
        private OperationStatus operationStatus;
    }


    // Bu sınıf, <metadata> altında bir <operationstatus> etiketi bekler.
    // Bu etiket boştur ve tüm hataları öznitelik olarak taşır.
    @Data
    public static class OperationStatus {

        // operationstatus businesserror="false" (Nitelikleri yakalar)
        @JacksonXmlProperty(isAttribute = true)
        @JsonAlias("businesserror") // XML'deki öznitelik adı
        private String businessError;

        // operationstatus technicalerror="false"
        @JacksonXmlProperty(isAttribute = true)
        @JsonAlias("technicalerror") // XML'deki öznitelik adı
        private String technicalError;

        // operationstatus calculationerror="false"
        @JacksonXmlProperty(isAttribute = true)
        @JsonAlias("calculationerror") // XML'deki öznitelik adı
        private String calculationError;
    }
}