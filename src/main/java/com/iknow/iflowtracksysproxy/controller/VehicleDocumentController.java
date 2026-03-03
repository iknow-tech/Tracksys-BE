package com.iknow.iflowtracksysproxy.controller;

import com.iknow.iflowtracksysproxy.dto.request.DealerContractUpdateRequest;
import com.iknow.iflowtracksysproxy.dto.request.VehicleDocumentUpdateRequest;
import com.iknow.iflowtracksysproxy.entity.VehicleDocumentAssignment;
import com.iknow.iflowtracksysproxy.service.VehicleDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vehicle-document")
@RequiredArgsConstructor
public class VehicleDocumentController {

    private final VehicleDocumentService vehicleDocumentService;

    @PutMapping("/update")
    public ResponseEntity<VehicleDocumentAssignment> updateVehicleDocument(@RequestBody VehicleDocumentUpdateRequest request
    ) {
        VehicleDocumentAssignment vehicleDocumentAssignment= vehicleDocumentService.updateVehicleDocument(request);
        return ResponseEntity.ok(vehicleDocumentAssignment);
    }
}
