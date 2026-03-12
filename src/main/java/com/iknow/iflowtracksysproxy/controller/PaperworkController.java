package com.iknow.iflowtracksysproxy.controller;

import com.iknow.iflowtracksysproxy.dto.request.SendLicenseRequest;
import com.iknow.iflowtracksysproxy.service.PaperworkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/paperwork")
@RequiredArgsConstructor
@Slf4j
public class PaperworkController {

    private final PaperworkService paperworkService;

    @PostMapping("/send-license")
    public ResponseEntity<?> sendRuhsatToPaperwork(@RequestBody SendLicenseRequest request) {
        try {
            if (request.getPlateNo() == null || request.getPlateNo().isBlank()) {
                return ResponseEntity.badRequest().body("plateNumber required");
            }
            if (request.getChassisNo()== null || request.getChassisNo().isBlank()) {
                return ResponseEntity.badRequest().body("chassisNo required");
            }
            if (request.getExpirationDate()== null || request.getExpirationDate().isBlank()) {
                return ResponseEntity.badRequest().body("expirationDate required");
            }

            paperworkService.saveLicenseFile(
                    request.getPlateNo(),
                    request.getChassisNo(),
                    request.getExpirationDate()
            );

            return ResponseEntity.ok("License file Paperwork system send successful");

        } catch (Exception e) {
            log.error("Paperwork send error: ", e);
            return ResponseEntity.internalServerError()
                    .body("Paperwork send error: " + e.getMessage());
        }
    }
}
