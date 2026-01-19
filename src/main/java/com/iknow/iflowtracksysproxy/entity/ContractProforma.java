package com.iknow.iflowtracksysproxy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractProforma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contract_id", nullable = false)
    private String contractId;

    @Column(name = "dealer_business_partner_id", nullable = false)
    private String dealerBusinessPartnerId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "status", nullable = false, length = 30)
    private String  status;

    @Column(length = 2000)
    private String approvalNote;

    private String approvedBy;

    private LocalDateTime approvedAt;

    private LocalDateTime uploadedAt;

}
