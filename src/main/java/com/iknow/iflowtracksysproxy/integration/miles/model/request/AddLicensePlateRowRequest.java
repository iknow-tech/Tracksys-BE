package com.iknow.iflowtracksysproxy.integration.miles.model.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class AddLicensePlateRowRequest {
    private String fleetVehicleId;
    private String licenseNumber;
    private LocalDate registrationDate;
}
