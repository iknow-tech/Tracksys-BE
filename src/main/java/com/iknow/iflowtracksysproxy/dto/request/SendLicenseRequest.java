package com.iknow.iflowtracksysproxy.dto.request;

import lombok.Data;

@Data
public class SendLicenseRequest {
    private String contractId;
    private String plateNo;
    private String chassisNo;
    private String expirationDate;
}
