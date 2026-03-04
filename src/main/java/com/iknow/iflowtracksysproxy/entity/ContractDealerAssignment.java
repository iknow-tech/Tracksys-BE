package com.iknow.iflowtracksysproxy.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ContractDealerAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contract_id", nullable = false, length = 50)
    private String contractId;

    @Column(name = "dealer_business_partner_id", nullable = false, length = 50)
    private String dealerBusinessPartnerId;

    @Column(name = "dealer_contact_id", length = 50)
    private String dealerContactId;

    @Column(name = "dealer_name", nullable = false)
    private String dealerName;

    @Column(name = "assigned_by", length = 100)
    private String assignedBy;

    @Column(name = "assigned_date")
    private LocalDateTime assignedDate;

    @Column(name = "net_price")
    private BigDecimal netPrice;

    @Column(name = "otv")
    private BigDecimal otv;

    @Column(name = "chassis_number")
    private String chassisNumber;

    @Column(name = "motor_number")
    private String motorNumber;

    @Column(name = "delivery")
    private String delivery;

    @Column(name = "shipment_begin_date")
    private String shipmentBeginDate;

    @Column(name = "shipment_end_date")
    private String shipmentEndDate;

    @Column(name = "ettn")
    private String ettn;

    @Column(name = "deliveryDate")
    private String deliveryDate;

    @Column(name = "leasing_invoice_date")
    private LocalDate leasingInvoiceDate;

    @Column(name = "delivery_method")
    private String deliveryMethod;

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    //sipariş durumu
    private ContractStatus contractStatus= ContractStatus.ACTIVE;

    private String cancelledBy;
    private LocalDateTime cancelledDate;
    private String updatedBy;
    private LocalDateTime updatedDate;
    private LocalDateTime completedDate;
    private String completedBy;

    // bayinin keseceği fatura için; satın alma biriminin bayiye gönderdiği mail tarihi
    private LocalDateTime dealerInvoiceMailSentAt;


}
