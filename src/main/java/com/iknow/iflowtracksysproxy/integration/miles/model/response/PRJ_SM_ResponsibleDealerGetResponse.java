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
public class PRJ_SM_ResponsibleDealerGetResponse {

    @JacksonXmlProperty(localName = "data")
    private ResponseData data;

    @JacksonXmlProperty(localName = "metadata")
    private ResponseMetadata metadata;

    @Data
    public static class ResponseData {
        @JacksonXmlProperty(localName = "PRJ_SM_ResponsibleDealer_Set")
        private ResponsibleDealerSet responsibleDealerSet;
    }

    @Data
    public static class ResponsibleDealerSet {
        @JacksonXmlProperty(localName = "PRJ_SM_ResponsibleDealer")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<ResponsibleDealer> responsibleDealers;
    }

    @Data
    public static class ResponsibleDealer {
        @JacksonXmlProperty(localName = "customerbusinesspartner_id")
        private DealerField customerBusinessPartnerId;

        @JacksonXmlProperty(localName = "customername")
        private DealerField customerName;

        @JacksonXmlProperty(localName = "advisorbusinesspartner_id")
        private DealerField advisorBusinessPartnerId;

        @JacksonXmlProperty(localName = "advisor")
        private DealerField advisor;
    }

    @Data
    public static class DealerField {
        @JacksonXmlProperty(isAttribute = true)
        private String type;

        @JacksonXmlProperty(isAttribute = true)
        private String isNull;

        @JacksonXmlProperty(isAttribute = true)
        private String title;

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