package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse {
    @JsonProperty("data")
    private DataResponse data;

    @JsonProperty("metadata")
    private MetadataResponse metadata;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MetadataResponse {
        @JacksonXmlProperty(localName = "operationstatus")
        private OperationStatus operationstatus;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OperationStatus {
        @JacksonXmlProperty(isAttribute = true)
        @JsonAlias("businesserror")
        private String businesserror;

        @JacksonXmlProperty(isAttribute = true)
        @JsonAlias("technicalerror")
        private String technicalerror;

        @JacksonXmlProperty(isAttribute = true)
        @JsonAlias("calculationerror")
        private String calculationerror;
    }
}
