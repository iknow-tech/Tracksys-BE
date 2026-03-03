package com.iknow.iflowtracksysproxy.integration.miles.model.response;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Data;

import java.util.List;

@JacksonXmlRootElement(localName = "SofEnvelope")
@Data
public class VehicleInspectionUpdateResponse {
    @JacksonXmlProperty(localName = "data")
    private ResponseData data;

    @JacksonXmlProperty(localName = "metadata")
    private ResponseMetadata metadata;

    // ---------- DATA BÖLÜMÜ ----------
    @Data
    public static class ResponseData {
        @JacksonXmlProperty(localName = "PRJ_SM_VehicleDocuments_Set")
        private VehicleDocumentsSet vehicleDocumentsSet;
    }

    @Data
    public static class VehicleDocumentsSet {
        @JacksonXmlProperty(localName = "PRJ_SM_VehicleDocuments")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<VehicleDocument> vehicleDocuments;
    }

    @Data
    public static class VehicleDocument {
        @JacksonXmlProperty(localName = "licensedocumentnumber")
        private DocumentValue licenseDocumentNumber;

        @JacksonXmlProperty(localName = "vehicleinspection")
        private DocumentValue vehicleInspection;

        @JacksonXmlProperty(localName = "hgstagno")
        private DocumentValue hgsTagNo;

        @JacksonXmlProperty(localName = "licenseplateandequipmentrequestdate")
        private DocumentValue licensePlateRequestDate;

        @JacksonXmlProperty(localName = "trafficinsurance")
        private DocumentValue trafficInsurance;
    }

    // Etiketlerin içindeki type, title ve visibilityType attribute'larını karşılayan sınıf
    @Data
    public static class DocumentValue {
        @JacksonXmlProperty(isAttribute = true)
        private String type;

        @JacksonXmlProperty(isAttribute = true)
        private String title;

        @JacksonXmlProperty(isAttribute = true)
        private String visibilityType;

        @JacksonXmlText
        private String value;
    }

    // ---------- METADATA BÖLÜMÜ ----------
    @Data
    public static class ResponseMetadata {
        @JacksonXmlProperty(localName = "operationstatus")
        private OperationStatus operationStatus;
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
