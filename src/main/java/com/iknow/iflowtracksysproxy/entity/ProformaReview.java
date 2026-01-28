package com.iknow.iflowtracksysproxy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProformaReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String contractId;

    @Lob
    @Column(nullable = false)
    private String description;

    private ReviewType target;

    private ReviewType source;

    @Enumerated(EnumType.STRING)
    private ReviewStatus status;

    @Enumerated(EnumType.STRING)
    private NotificationStatus notificationStatus;

    // Satın alma biriminin yüklediği ek belge
    @Column(name = "additional_document_path")
    private String additionalDocumentPath;

    // Dosya adı (bayi görecek)
    @Column(name = "additional_document_name")
    private String additionalDocumentName;

    // Bu kayıt ek belge talebi mi?
    @Column(nullable = false)
    private Boolean additionalDocumentRequested;

    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;


}
