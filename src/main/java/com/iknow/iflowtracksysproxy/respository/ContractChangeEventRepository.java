package com.iknow.iflowtracksysproxy.respository;

import com.iknow.iflowtracksysproxy.entity.ContractChangeEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractChangeEventRepository extends JpaRepository<ContractChangeEvent, Long> {

    List<ContractChangeEvent> findByContractIdAndSeenByDealerOrderByChangedAtDesc(String contractId, boolean seenByDealer);

    long countByContractIdAndSeenByDealerFalse(String contractId);
}
