package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class GetDealerResponse {
    @JsonAlias("businesspartner_id")
    private String businessPartnerId;
    @JsonAlias("dealer")
    private String dealer;
    @JsonAlias("contact_id")
    private String contactId;
    @JsonAlias("contactname")
    private String contactName;
}
