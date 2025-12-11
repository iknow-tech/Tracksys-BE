package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class DataResponse {
    @JsonProperty("SofToken")
    private LogonResponse loginResponse;

    public LogonResponse getLoginResponse() {
        return loginResponse;
    }

    public void setLoginResponse(LogonResponse loginResponse) {
        this.loginResponse = loginResponse;
    }
}
