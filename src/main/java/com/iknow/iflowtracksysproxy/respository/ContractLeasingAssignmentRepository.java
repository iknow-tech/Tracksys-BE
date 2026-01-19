package com.iknow.iflowtracksysproxy.respository;

import com.iknow.iflowtracksysproxy.entity.ContractDealerAssignment;
import com.iknow.iflowtracksysproxy.entity.ContractLeasingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractLeasingAssignmentRepository extends JpaRepository<ContractLeasingAssignment, Long> {

    List<ContractLeasingAssignment> findByStatus(String status);

    Optional<ContractLeasingAssignment> findByContractIdAndStatus(String contractId, String status);

    @Modifying
    @Query("""
   UPDATE ContractLeasingAssignment c
   SET c.status = 'PASSIVE'
   WHERE c.contractId = :contractId
     AND c.status = 'ACTIVE'
""")
    void passiveAllActiveByContractId(@Param("contractId") String contractId);


}
