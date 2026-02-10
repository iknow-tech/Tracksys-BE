package com.iknow.iflowtracksysproxy.controller;

import com.iknow.iflowtracksysproxy.entity.DeliveryDocument;
import com.iknow.iflowtracksysproxy.service.DeliveryDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/delivery-document")
@RequiredArgsConstructor
public class DeliveryDocumentController {

    private final DeliveryDocumentService deliveryDocumentService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDeliveryDocument(@RequestParam String contractId, @RequestParam String dealerBusinessPartnerId, @RequestParam MultipartFile file, @RequestParam(required = false) String uploadedBy
    ) {
        DeliveryDocument document = deliveryDocumentService.upload(contractId, dealerBusinessPartnerId, file, uploadedBy);
        return ResponseEntity.ok(
                Map.of("documentId", document.getId(),
                        "fileName", document.getFileName(),
                        "uploadedAt", document.getUploadedAt()
                )
        );
    }


    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadDeliveryDocument(@PathVariable Long id) {
        return deliveryDocumentService.download(id);
    }


}
