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
public class DeliveryDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contract_id", nullable = false)
    private String contractId;

    @Column(name = "dealer_business_partner_id", nullable = false)
    private String dealerBusinessPartnerId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    private LocalDateTime uploadedAt;

    private String uploadedBy;

    @Column(name = "status", length = 30)
    private String  status;









}
