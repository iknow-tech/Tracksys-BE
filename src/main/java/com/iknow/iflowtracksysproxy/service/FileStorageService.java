package com.iknow.iflowtracksysproxy.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final String BASE_DIR = "uploads/proforma-review";

    public String store(MultipartFile file) {
        try {
            // klasör yoksa oluştur
            Path uploadDir = Paths.get(BASE_DIR);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // dosya adı çakışmasın diye UUID
            String fileName =
                    UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path targetPath = uploadDir.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return targetPath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Dosya kaydedilemedi", e);
        }
    }
}
