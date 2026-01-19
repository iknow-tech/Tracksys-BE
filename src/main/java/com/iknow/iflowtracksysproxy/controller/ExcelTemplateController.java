package com.iknow.iflowtracksysproxy.controller;

import com.iknow.iflowtracksysproxy.service.ExcelTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/excel")
@RequiredArgsConstructor

public class ExcelTemplateController {

    private final ExcelTemplateService excelTemplateService;

    @GetMapping("/contract-template")
    public ResponseEntity<byte[]> downloadContractTemplate() {
        byte[] file = excelTemplateService.generateContractTemplate();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=contract-template.xlsx")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(file);
    }


}
