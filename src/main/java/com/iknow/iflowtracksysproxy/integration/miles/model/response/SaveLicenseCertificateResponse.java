package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "Envelope", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
public class SaveLicenseCertificateResponse {

    @JacksonXmlProperty(localName = "Body", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
    private ResponseBody body;

    @Data
    public static class ResponseBody {
        @JacksonXmlProperty(localName = "SaveRuhsatBelgesiResponse", namespace = "http://tempuri.org/")
        private SaveRuhsatBelgesiResponse saveRuhsatBelgesiResponse;
    }

    @Data
    public static class SaveRuhsatBelgesiResponse {
        @JacksonXmlProperty(localName = "SaveRuhsatBelgesiResult", namespace = "http://tempuri.org/")
        private SaveRuhsatBelgesiResult saveRuhsatBelgesiResult;
    }

    @Data
    public static class SaveRuhsatBelgesiResult{
        @JacksonXmlProperty(localName = "HATA_KODU")
        private String status;
        @JacksonXmlProperty(localName = "HATA_MESAJI")
        private String message;
    }

    /**
     * Eğer Miles servisinin standart metadata yapısını bu SOAP cevabına
     * manuel olarak ekleyecekseniz aşağıdakileri kullanabilirsiniz.
     * Normal şartlarda görseldeki SOAP çıktısında metadata alanı bulunmamaktadır.
     */
    @Data
    public static class ResponseMetadata {
        @JacksonXmlProperty(localName = "operationstatus")
        private OperationStatus operationStatus;
    }

    @Data
    public static class OperationStatus {
        @JacksonXmlProperty(isAttribute = true)
        @JsonAlias("businesserror")
        private String businessError;

        @JacksonXmlProperty(isAttribute = true)
        @JsonAlias("technicalerror")
        private String technicalError;

        @JacksonXmlProperty(isAttribute = true)
        @JsonAlias("calculationerror")
        private String calculationError;
    }
}