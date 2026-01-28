package com.iknow.iflowtracksysproxy.entity;

public enum ContractFieldKey {
    DELIVERY_LOCATION("Teslimat İli"),
    COLOR("Renk"),
    MODEL_YEAR("Model Yılı"),
    OPTIONS("Opsiyonlar"),
    UTTS_GPS_INSTALLATION("UTTS & GPS Montajı"),
    TREASURY_APPROVAL_DATE("Hazine Onay Tarihi"),
    DELIVERY_PERSON("Teslim Edilecek Kişi"),
    DELIVERY_TERMS("Teslimat Şartı"),
    ORDERSID("İş Emri No"),
    DELIVERY_DATE("Teslim Tarihi");   //?



    private final String label;

    ContractFieldKey(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

