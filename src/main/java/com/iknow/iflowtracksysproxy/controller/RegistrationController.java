package com.iknow.iflowtracksysproxy.controller;

import com.iknow.iflowtracksysproxy.service.AIService;
import com.iknow.iflowtracksysproxy.service.OcrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/ruhsat")
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {

    private final OcrService ocrService;
    private final AIService aiService;

    @PostMapping(value = "/analiz-et", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> ruhsatYukleVeAnalizEt(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Lütfen bir resim dosyası yükleyin.");
            }

            // OCR
            String ocrMetni = ocrService.scanImage(file);

            if (ocrMetni == null || ocrMetni.length() < 10) {
                return ResponseEntity.badRequest().body("Resimden anlamlı bir metin okunamadı. Lütfen daha net bir fotoğraf yükleyin.");
            }

            // AI
            String jsonSonuc = aiService.parseRuhsatInfo(ocrMetni);

            // Direkt JSON string olarak dönüyoruz, Frontend bunu parse edip kullanabilir.
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonSonuc);

        } catch (Exception e) {
            log.error("Ruhsat analiz sürecinde hata: ", e);
            return ResponseEntity.internalServerError().body("İşlem sırasında bir hata oluştu: " + e.getMessage());
        }
    }
}