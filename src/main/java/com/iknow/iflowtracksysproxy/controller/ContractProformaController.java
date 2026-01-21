package com.iknow.iflowtracksysproxy.controller;

import com.iknow.iflowtracksysproxy.entity.ContractProforma;
import com.iknow.iflowtracksysproxy.service.ContractProformaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;


import java.util.List;

@RestController
@RequestMapping("/api/v1/proforma")
@RequiredArgsConstructor
@Slf4j
public class ContractProformaController {

    private final ContractProformaService proformaService;

    @PostMapping("/upload")
    public ResponseEntity<List<ContractProforma>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("contractIds") List<String> contractIdsJson,
            @RequestParam("dealerBusinessPartnerId") String dealerId
    ) {
        List<ContractProforma> proforma = proformaService.upload(file, contractIdsJson, dealerId);
        return new ResponseEntity<>(proforma, HttpStatus.OK);
    }

    @GetMapping("/contract/{contractId}")
    public ResponseEntity<List<ContractProforma>> getByContract( @PathVariable String contractId) {
        return ResponseEntity.ok(
                proformaService.getByContractId(contractId)
        );
    }

    @GetMapping("/download/{proformaId}")
    public ResponseEntity<Resource> download(@PathVariable String proformaId) {

        ContractProforma proforma = proformaService.findById(proformaId)
                .orElseThrow(() -> new RuntimeException("Proforma bulunamadı"));

        Resource resource = proformaService.loadProformaFile(proformaId);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + proforma.getFileName() + "\""
                )
                .contentType(MediaType.parseMediaType(proforma.getContentType()))
                .body(resource);
    }




}
