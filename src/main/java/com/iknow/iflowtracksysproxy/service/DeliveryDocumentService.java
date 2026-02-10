package com.iknow.iflowtracksysproxy.service;

import com.iknow.iflowtracksysproxy.dto.AdditionalDocumentRequestDto;
import com.iknow.iflowtracksysproxy.entity.DeliveryDocument;
import com.iknow.iflowtracksysproxy.entity.ReviewType;
import com.iknow.iflowtracksysproxy.respository.DeliveryDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryDocumentService {

    private final DeliveryDocumentRepository deliveryDocumentRepository;
    private final FileStorageService fileStorageService;
    private final ProformaReviewService notificationService;

    @Transactional
    public DeliveryDocument upload(String contractId, String dealerBusinessPartnerId, MultipartFile file, String uploadedBy
    ) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Dosya boş olamaz");
        }

        if (deliveryDocumentRepository.findByContractId(contractId).isPresent()) {
            throw new IllegalStateException(
                    "Bu kontrata ait teslimat belgesi zaten yüklenmiş"
            );
        }

        String extension = getExtension(file.getOriginalFilename());
        String filePath = fileStorageService.store(file);
        DeliveryDocument document = DeliveryDocument.builder()
                .contractId(contractId)
                .dealerBusinessPartnerId(dealerBusinessPartnerId)
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .filePath(filePath)
                .uploadedAt(LocalDateTime.now())
                .uploadedBy(uploadedBy)
                .build();

        DeliveryDocument saved = deliveryDocumentRepository.save(document);

        log.info(
                "Delivery document uploaded for contract {}",
                contractId
        );
        // BURADA satınalma bildirimi üretilecek
        AdditionalDocumentRequestDto additionalDocumentRequestDto = new AdditionalDocumentRequestDto();
        additionalDocumentRequestDto.setContractId(contractId);
        additionalDocumentRequestDto.setDescription("İlgili Bayi Tarafından Araç Teslim Formu Yüklendi.");
        additionalDocumentRequestDto.setTarget(ReviewType.PURCHASING);

        notificationService.createAdditionalDocumentRequest(additionalDocumentRequestDto,file);
        return saved;
    }

    public ResponseEntity<Resource> download(Long id) {

        DeliveryDocument document = deliveryDocumentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Teslimat belgesi bulunamadı"));

        Path filePath = Paths.get(document.getFilePath());

        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Dosya okunamadı", e);
        }

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Dosya erişilebilir değil");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getContentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + document.getFileName() + "\""
                )
                .body(resource);
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
