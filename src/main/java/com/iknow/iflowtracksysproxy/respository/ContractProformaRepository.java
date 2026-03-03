package com.iknow.iflowtracksysproxy.respository;

import com.iknow.iflowtracksysproxy.entity.ContractLeasingAssignment;
import com.iknow.iflowtracksysproxy.entity.ContractProforma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractProformaRepository extends JpaRepository<ContractProforma, Long> {

    List<ContractProforma> findByContractIdOrderByUploadedAtDesc(String contractId);

    boolean existsByContractId(String contractId);

}
