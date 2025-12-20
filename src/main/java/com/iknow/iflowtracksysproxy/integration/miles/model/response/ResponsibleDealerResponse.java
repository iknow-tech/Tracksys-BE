package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class ResponsibleDealerResponse {
    @JsonAlias("customerbusinesspartner_id")
    private String customerBusinessPartnerId;
    @JsonAlias("customername")
    private String customerName;
    @JsonAlias("advisorbusinesspartner_id")
    private String advisorBusinessPartnerId;
    @JsonAlias("advisor")
    private String advisor;
}
