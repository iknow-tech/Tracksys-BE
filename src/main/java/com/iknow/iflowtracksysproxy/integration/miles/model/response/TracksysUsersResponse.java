package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import java.util.List;

@Data
@JacksonXmlRootElement(localName = "SofEnvelope")
public class TracksysUsersResponse {

    @JacksonXmlProperty(localName = "data")
    private ResponseData data;

    @JacksonXmlProperty(localName = "metadata")
    private ResponseMetadata metadata;

    @Data
    public static class ResponseData {
        @JacksonXmlProperty(localName = "PRJ_SM_TracksysUsers_Set")
        private TracksysUsersSet tracksysUsersSet;
    }

    @Data
    public static class TracksysUsersSet {

        // Liste yapısı olduğu için wrapper'ı false yapıyoruz,
        // çünkü PRJ_SM_TracksysUsers_Set zaten kapsayıcı sınıfımız.
        @JacksonXmlProperty(localName = "PRJ_SM_TracksysUsers")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<TracksysUser> tracksysUsers;
    }

    @Data
    public static class TracksysUser {

        @JacksonXmlProperty(localName = "contact_id")
        private Long contactId;

        @JacksonXmlProperty(localName = "identification")
        private String identification;

        @JacksonXmlProperty(localName = "tracksysemail")
        private String tracksysEmail;

        @JacksonXmlProperty(localName = "description")
        private String description;

        @JacksonXmlProperty(localName = "useraccount_id")
        private Long userAccountId;
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