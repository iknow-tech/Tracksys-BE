package com.iknow.iflowtracksysproxy.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@Slf4j
public class PaperworkService {

    private static final String ENDPOINT = "http://ts021hfistpw01.hedeffilotest.net:81/SaveRuhsatBelgesiDGA.asmx";
    private static final String SOAP_ACTION = "http://tempuri.org/SaveRuhsatBelgesi";

    public void saveLicenseFile(String plakaNo, String sasiNo, String gecerlilikTarihi) throws Exception {
        saveLicenseFile(plakaNo, sasiNo, gecerlilikTarihi, null, null);
    }

    public void saveLicenseFile(
            String plateNumber,
            String chassisNumber,
            String expirationDate,
            String fileBinary,
            String fileExtension
    ) throws Exception {

        String soapBody = buildSoapEnvelope(plateNumber, chassisNumber, expirationDate, fileBinary, fileExtension);

        URL url = new URL(ENDPOINT);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        connection.setRequestProperty("SOAPAction", SOAP_ACTION);
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(30000);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(soapBody.getBytes("UTF-8"));
            os.flush();
        }

        int responseCode = connection.getResponseCode();
        StringBuilder response = new StringBuilder();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        responseCode == 200 ? connection.getInputStream() : connection.getErrorStream(),
                        "UTF-8"
                )
        )) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line).append("\n");
            }
        }

        if (responseCode != 200) {
            throw new RuntimeException("SOAP isteği başarısız. HTTP " + responseCode + ":\n" + response);
        }

        log.info("Paperwork SOAP başarılı. plakaNo={}, sasiNo={}", plateNumber, chassisNumber);
    }

    private String buildSoapEnvelope(
            String plateNumber,
            String chassisNumber,
            String expirationDate,
            String fileBinary,
            String fileExtension
    ) {
        String binaryTag = fileBinary != null
                ? "<tem:dokumanBinary>" + fileBinary + "</tem:dokumanBinary>"
                : "<!--Optional:-->";

        String extensionTag = fileExtension != null
                ? "<tem:dokumanExtension>" + escapeXml(fileExtension) + "</tem:dokumanExtension>"
                : "<!--Optional:-->";

        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                "xmlns:tem=\"http://tempuri.org/\">" +
                "  <soapenv:Header/>" +
                "  <soapenv:Body>" +
                "    <tem:SaveRuhsatBelgesi>" +
                "      <tem:plakaNo>" + escapeXml(plateNumber) + "</tem:plakaNo>" +
                "      <tem:sasiNo>" + escapeXml(chassisNumber) + "</tem:sasiNo>" +
                "      <tem:gecerlilikTarihi>" + escapeXml(expirationDate) + "</tem:gecerlilikTarihi>" +
                "      " + binaryTag +
                "      " + extensionTag +
                "    </tem:SaveRuhsatBelgesi>" +
                "  </soapenv:Body>" +
                "</soapenv:Envelope>";
    }

    private String escapeXml(String value) {
        if (value == null) return "";
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

}
