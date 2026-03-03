package com.iknow.iflowtracksysproxy.service;

import com.azure.ai.vision.imageanalysis.ImageAnalysisClient;
import com.azure.ai.vision.imageanalysis.ImageAnalysisClientBuilder;
import com.azure.ai.vision.imageanalysis.models.VisualFeatures;
import com.azure.core.credential.KeyCredential;
import com.azure.core.util.BinaryData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

@Service
@Slf4j
public class OcrService {

    private final String visionEndpoint;
    private final String visionKey;
    private ImageAnalysisClient client;

    // Constructor Injection ile değerleri alıyoruz
    public OcrService(@Value("${azure.vision.endpoint}") String visionEndpoint,
                            @Value("${azure.vision.key}") String visionKey) {
        this.visionEndpoint = visionEndpoint;
        this.visionKey = visionKey;
        initClient();
    }

    private void initClient() {
        try {
            this.client = new ImageAnalysisClientBuilder()
                    .credential(new KeyCredential(visionKey))
                    .endpoint(visionEndpoint)
                    .buildClient();
            log.info("Azure Vision Client başarıyla başlatıldı.");
        } catch (Exception e) {
            log.error("Azure Vision Client başlatılamadı: {}", e.getMessage());
        }
    }

    public String scanImage(MultipartFile file) throws IOException {
        log.info("Ruhsat OCR taraması başladı...");

        // MultipartFile'ı Azure BinaryData'ya çevir
        BinaryData imageData = BinaryData.fromBytes(file.getBytes());

        try {
            var result = client.analyze(
                    imageData,
                    Arrays.asList(VisualFeatures.READ), // Sadece okuma yap
                    null
            );

            if (result.getRead() != null) {
                StringBuilder sb = new StringBuilder();
                result.getRead().getBlocks().forEach(block ->
                        block.getLines().forEach(line ->
                                sb.append(line.getText()).append(" ")
                        )
                );
                String ocrText = sb.toString().trim();
                log.info("OCR Başarılı. Okunan karakter sayısı: {}", ocrText.length());
                return ocrText;
            }
        } catch (Exception e) {
            log.error("OCR Tarama Hatası: {}", e.getMessage());
            throw new RuntimeException("OCR işlemi başarısız oldu: " + e.getMessage());
        }
        return "";
    }
}