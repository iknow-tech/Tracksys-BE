package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class GetLeasingResponse {
    @JsonAlias("sysenumeration_id")
    private String sysEnumerationId;
    @JsonAlias("ownership")
    private String description;
}
