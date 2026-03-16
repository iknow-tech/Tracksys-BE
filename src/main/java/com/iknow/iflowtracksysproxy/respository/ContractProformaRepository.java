package com.iknow.iflowtracksysproxy.respository;

import com.iknow.iflowtracksysproxy.entity.ContractLeasingAssignment;
import com.iknow.iflowtracksysproxy.entity.ContractProforma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractProformaRepository extends JpaRepository<ContractProforma, Long> {

    List<ContractProforma> findByContractIdOrderByUploadedAtDesc(String contractId);

    boolean existsByContractId(String contractId);

    @Query("SELECT c.contractId FROM ContractProforma c WHERE c.contractId IN :ids")
    List<String> findContractIdsByContractIdIn(@Param("ids") List<String> ids);

}
