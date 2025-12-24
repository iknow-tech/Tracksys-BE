package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "SofEnvelope")
public class PRJ_SM_OwnerShipResponse {

    @JacksonXmlProperty(localName = "data")
    private ResponseData data;

    @JacksonXmlProperty(localName = "metadata")
    private ResponseMetadata metadata;

    @Data
    public static class ResponseData {
        @JacksonXmlProperty(localName = "PRJ_SM_OwnerShip_Set")
        private OwnerShipSet ownerShipSet;
    }

    @Data
    public static class OwnerShipSet {
        @JacksonXmlProperty(localName = "PRJ_SM_OwnerShip")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<OwnerShipItem> ownerShips;
    }

    @Data
    public static class OwnerShipItem {
        @JacksonXmlProperty(localName = "sysenumeration_id")
        private SysEnumerationId sysEnumerationId;

        @JacksonXmlProperty(localName = "ownership")
        private OwnershipDetail ownership;
    }

    // sysenumeration_id etiketi için özel sınıf
    @Data
    public static class SysEnumerationId {
        @JacksonXmlProperty(isAttribute = true)
        private String title;

        @JacksonXmlProperty(isAttribute = true)
        private String type;

        @JacksonXmlProperty(isAttribute = true)
        private String visibilityType;

        @JacksonXmlText
        private String value;
    }

    // ownership etiketi için özel sınıf
    @Data
    public static class OwnershipDetail {
        @JacksonXmlProperty(isAttribute = true)
        private String isNull;

        @JacksonXmlProperty(isAttribute = true)
        private String title;

        @JacksonXmlProperty(isAttribute = true)
        private String type;

        @JacksonXmlProperty(isAttribute = true)
        private String visibilityType;

        @JacksonXmlText
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