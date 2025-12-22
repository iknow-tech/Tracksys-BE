package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "SofEnvelope")
public class TrafficInsuranceGetResponse {

    @JacksonXmlProperty(localName = "data")
    private ResponseData data;

    @JacksonXmlProperty(localName = "metadata")
    private ResponseMetadata metadata;

    @Data
    public static class ResponseData {
        @JacksonXmlProperty(localName = "PRJ_SM_VehicleDocuments_Set")
        private VehicleDocumentsSet vehicleDocumentsSet;
    }

    @Data
    public static class VehicleDocumentsSet {
        @JacksonXmlProperty(localName = "PRJ_SM_VehicleDocuments")
        private VehicleDocuments vehicleDocuments;
    }

    @Data
    public static class VehicleDocuments {
        @JacksonXmlProperty(localName = "licensedocumentnumber")
        private double licenseDocumentNumber;

        @JacksonXmlProperty(localName = "vehicleinspection")
        private double vehicleInspection;

        @JacksonXmlProperty(localName = "hgstagno")
        private double hgsTagNo;

        @JacksonXmlProperty(localName = "licenseplateandequipmentrequestdate")
        private double licensePlateRequestDate;

        @JacksonXmlProperty(localName = "trafficinsurance")
        private double trafficInsurance;
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