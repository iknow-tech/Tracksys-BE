package com.iknow.iflowtracksysproxy.integration.miles.model.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SaveLicenseCertificateRequest {
    private String plakaNo;
    private String sasiNo;
    private String validityDate;
    private MultipartFile file;   // 👈 DOSYA BURADA
}
