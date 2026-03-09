package com.iknow.iflowtracksysproxy.entity;

public enum ContractOrderStatus {

    SHIPMENT_WILL_START("Sevk Başlayacak"),
    SHIPMENT_IN_PROGRESS("Sevk Devam Ediyor"),
    SHIPMENT_DONE_WAITING_DELIVERY_CONDITION("Sevk Bitti, Teslimat Şartının Kalkması Bekleniyor"),
    SHIPMENT_DONE_DELIVERY_TO_BE_PLANNED("Sevk Bitti, Teslimat Planlanacak"),
    DELIVERY_PLANNED("Teslimat Planlandı"),
    ORDER_DELIVERED("Sipariş Teslim Edildi"),
    ORDER_CANCELLED("Sipariş İptal Edildi");

    private final String label;

    ContractOrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
