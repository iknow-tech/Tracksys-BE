package com.iknow.iflowtracksysproxy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelTemplateService {

    public byte[] generateContractTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Template");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Contract Id");
            header.createCell(1).setCellValue("Net Bedel");
            header.createCell(2).setCellValue("Ötv");
            header.createCell(3).setCellValue("Şasi No");
            header.createCell(4).setCellValue("Motor No");


            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Excel şablonu oluşturulamadı", e);
        }
    }
}
