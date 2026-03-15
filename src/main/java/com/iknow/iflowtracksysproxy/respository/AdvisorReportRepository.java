package com.iknow.iflowtracksysproxy.respository;

import com.iknow.iflowtracksysproxy.entity.AdvisorReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdvisorReportRepository extends JpaRepository<AdvisorReport, Long> {
    Optional<AdvisorReport> findByContractId(String contractId);
}