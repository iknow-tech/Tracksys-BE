package com.iknow.iflowtracksysproxy.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractLeasingAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contract_id", nullable = false, length = 50)
    private String contractId;

    @Column(name = "leasing_enum_id", length = 50)
    private String leasingEnumId;

    @Column(name = "leasing_name", nullable = true, length = 50)
    private String leasingName;

    @Column(name = "assigned_by", length = 100)
    private String assignedBy;

    @Column(name = "assigned_date")
    private LocalDateTime assignedDate;

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    private String cancelledBy;
    private LocalDateTime cancelledDate;

}
