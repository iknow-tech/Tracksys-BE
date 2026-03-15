package com.iknow.iflowtracksysproxy.respository;

import com.iknow.iflowtracksysproxy.entity.DealerReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DealerReportRepository extends JpaRepository<DealerReport, Long> {
    Optional<DealerReport> findByContractId(String contractId);
}