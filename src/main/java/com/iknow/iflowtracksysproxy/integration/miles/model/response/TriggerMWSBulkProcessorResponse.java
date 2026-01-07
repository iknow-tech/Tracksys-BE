package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TriggerMWSBulkProcessorResponse {

    @JsonProperty("data")
    private ResponseData data;

    @JsonProperty("metadata")
    private BaseResponse.MetadataResponse metadata;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ResponseData {
        @JsonProperty("MWSJobInstance")
        @JacksonXmlProperty(localName = "MWSJobInstance")
        private MWSJobInstance mwsJobInstance;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MWSJobInstance {
        @JsonProperty("id")
        private String id;

        @JsonProperty("state")
        private State state;

        @JsonProperty("groupname")
        private String groupname;

        @JsonProperty("name")
        private String name;

        @JsonProperty("starttime")
        private String starttime;

        @JsonProperty("endtime")
        private String endtime;

        @JsonProperty("errorcode")
        private String errorcode;

        @JsonProperty("systemerrors")
        private String systemerrors;

        @JsonProperty("businesserrors")
        private String businesserrors;

        @JsonProperty("recordsfound")
        private String recordsfound;

        @JsonProperty("recordsprocessed")
        private String recordsprocessed;

        @JsonProperty("bulkprocessorId")
        private String bulkprocessorId;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class State {
        @JacksonXmlProperty(isAttribute = true)
        private String id;

        @JacksonXmlProperty(isAttribute = true)
        private String type;

        @JacksonXmlProperty(isAttribute = true)
        private String group;

        @JacksonXmlProperty(isAttribute = true)
        private String value; // For the text content "Bitti"
    }
}
