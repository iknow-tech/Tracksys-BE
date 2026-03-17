package com.iknow.iflowtracksysproxy.respository;

import com.iknow.iflowtracksysproxy.entity.ContractDealerAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractDealerAssignmentRepository extends JpaRepository<ContractDealerAssignment, Long> {

    /**
     * Kontratın aktif atamasını bul
     */
    Optional<ContractDealerAssignment> findByContractIdAndStatus(String contractId, String status);

    Optional<ContractDealerAssignment> findByContractId(String contractId);

    /**
     * Bayinin aktif kontratlarını bul (TÜM OBJE)
     */
    @Query("""
    SELECT DISTINCT a
    FROM ContractDealerAssignment a
    WHERE a.dealerBusinessPartnerId = :dealerId
      AND a.status = :status
    ORDER BY a.assignedDate DESC
""")
    List<ContractDealerAssignment> findLatestAssignmentsPerContract(
            @Param("dealerId") String dealerId,
            @Param("status") String status
    );

    List<ContractDealerAssignment> findByStatus(String status);

    Optional<ContractDealerAssignment> findFirstByContractIdAndStatus(
            String contractId, String status);

    List<ContractDealerAssignment> findByDealerBusinessPartnerIdAndStatus(
            String dealerId, String status);

    List<ContractDealerAssignment> findByStatusAndContractIdIn(String status, List<String> contractIds);

    List<ContractDealerAssignment> findByContractIdIn(List<String> contractIds);



}
