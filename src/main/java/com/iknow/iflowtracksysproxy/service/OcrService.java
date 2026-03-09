package com.iknow.iflowtracksysproxy.service;

import com.azure.ai.vision.imageanalysis.ImageAnalysisClient;
import com.azure.ai.vision.imageanalysis.ImageAnalysisClientBuilder;
import com.azure.ai.vision.imageanalysis.models.VisualFeatures;
import com.azure.core.credential.KeyCredential;
import com.azure.core.util.BinaryData;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

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
        log.info("Ruhsat OCR taraması başladı. Dosya tipi: {}, Boyut: {}",
                file.getContentType(), file.getSize());

        try {
            byte[] imageBytes;
            String contentType = file.getContentType();

            // PDF ise ilk sayfayı PNG'ye çevir
            if (contentType != null && contentType.equals("application/pdf")) {
                imageBytes = convertPdfToImage(file.getBytes());
            } else {
                // Resim ise direkt BufferedImage üzerinden yeniden encode et
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
                if (bufferedImage == null) {
                    throw new RuntimeException("Geçersiz resim formatı");
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "PNG", baos);
                imageBytes = baos.toByteArray();
            }

            BinaryData imageData = BinaryData.fromBytes(imageBytes);
            var result = client.analyze(
                    imageData,
                    Arrays.asList(VisualFeatures.READ),
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
                log.info("OCR Başarılı. Karakter sayısı: {}", ocrText.length());
                return ocrText;
            }
        } catch (Exception e) {
            log.error("OCR Tarama Hatası: {}", e.getMessage());
            throw new RuntimeException("OCR işlemi başarısız oldu: " + e.getMessage());
        }
        return "";
    }

    private byte[] convertPdfToImage(byte[] pdfBytes) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image = renderer.renderImageWithDPI(0, 300);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            return baos.toByteArray();
        }
    }}