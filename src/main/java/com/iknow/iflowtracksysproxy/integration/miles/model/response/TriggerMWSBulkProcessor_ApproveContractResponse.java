package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "SofEnvelope")
public class TriggerMWSBulkProcessor_ApproveContractResponse {

    @JacksonXmlProperty(localName = "data")
    private ResponseData data;

    @JacksonXmlProperty(localName = "metadata")
    private ResponseMetadata metadata;

    @Data
    public static class ResponseData {
        @JacksonXmlProperty(localName = "MWSJobInstance")
        private MWSJobInstance mwsJobInstance;
    }

    @Data
    public static class MWSJobInstance {
        @JacksonXmlProperty(localName = "id")
        private String id;

        @JacksonXmlProperty(localName = "state")
        private State state;

        @JacksonXmlProperty(localName = "groupname")
        private String groupName;

        @JacksonXmlProperty(localName = "name")
        private String name;

        @JacksonXmlProperty(localName = "starttime")
        private String startTime;

        @JacksonXmlProperty(localName = "endtime")
        private String endTime;

        @JacksonXmlProperty(localName = "errorcode")
        private String errorCode;

        @JacksonXmlProperty(localName = "systemerrors")
        private Integer systemErrors;

        @JacksonXmlProperty(localName = "businesserrors")
        private Integer businessErrors;

        @JacksonXmlProperty(localName = "recordsfound")
        private Integer recordsFound;

        @JacksonXmlProperty(localName = "recordsprocessed")
        private Integer recordsProcessed;

        @JacksonXmlProperty(localName = "bulkprocessorId")
        private String bulkProcessorId;
    }

    @Data
    public static class State {
        @JacksonXmlProperty(isAttribute = true)
        private String id;

        @JacksonXmlProperty(isAttribute = true)
        private String type;

        @JacksonXmlProperty(isAttribute = true)
        private String group;

        @JacksonXmlText
        private String value; // XML'deki "Bitti" metni için
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