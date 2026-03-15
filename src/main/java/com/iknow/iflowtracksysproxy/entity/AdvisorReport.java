package com.iknow.iflowtracksysproxy.entity;

import com.iknow.iflowtracksysproxy.entity.base.ReportBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "report_advisor")
@Getter
@Setter
public class AdvisorReport extends ReportBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}