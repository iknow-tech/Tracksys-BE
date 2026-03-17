package com.iknow.iflowtracksysproxy.service;

import com.iknow.iflowtracksysproxy.entity.ContractProforma;
import com.iknow.iflowtracksysproxy.respository.ContractProformaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractProformaService {

    private final ContractProformaRepository repository;

    @Transactional
    @CacheEvict(value = "customerContracts", allEntries = true) // ✅ cache temizle
    public List<ContractProforma> upload( MultipartFile file,  List<String> contractIds, String dealerId) {

        if (contractIds == null || contractIds.isEmpty()) {
            throw new IllegalArgumentException("En az bir kontrat seçilmelidir.");
        }

        String filePath = saveFile(file);
        List<ContractProforma> contractProformaList = new ArrayList<>();

        for (String contractId : contractIds) {
            ContractProforma contractProforma = new ContractProforma();
            contractProforma.setContractId(contractId);
            contractProforma.setDealerBusinessPartnerId(dealerId);
            contractProforma.setFileName(file.getOriginalFilename())   ;
            contractProforma.setFilePath(filePath);
            contractProforma.setContentType(file.getContentType());
            contractProforma.setFileSize(file.getSize());
            contractProforma.setStatus("UPLOADED");
            contractProforma.setUploadedAt(LocalDateTime.now());

            contractProforma= repository.save(contractProforma);
            contractProformaList.add(contractProforma);
        }

        log.info("Proforma uploaded for {} contracts", contractIds.size());
        return contractProformaList;

    }

    private String saveFile(MultipartFile file) {
        try {
            Path dir = Paths.get("uploads/proformas");
            Files.createDirectories(dir);

            String fileName =
                    UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path path = dir.resolve(fileName);
            Files.copy(file.getInputStream(), path);

            return path.toString();
        } catch (IOException e) {
            throw new RuntimeException("Dosya kaydedilemedi", e);
        }
    }

    public List<ContractProforma> getByContractId(String contractId) {
        return repository.findByContractIdOrderByUploadedAtDesc(contractId);
    }

    public Optional<ContractProforma> findById(Long proformaId) {
        return repository.findById(proformaId);
    }

    public Resource loadProformaFile(String proformaId) {

        ContractProforma proforma = findById(Long.getLong(proformaId))
                .orElseThrow(() -> new RuntimeException("Proforma bulunamadı"));

        try {
            Path filePath = Paths.get(proforma.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                throw new RuntimeException("Dosya bulunamadı");
            }

            return resource;
        } catch (Exception e) {
            throw new RuntimeException("Dosya okunamadı", e);
        }
    }

}
