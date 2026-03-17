package com.iknow.iflowtracksysproxy.respository;

import com.iknow.iflowtracksysproxy.entity.ContractChangeEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContractChangeEventRepository extends JpaRepository<ContractChangeEvent, Long> {

    List<ContractChangeEvent> findByContractIdAndSeenByDealerOrderByChangedAtDesc(String contractId, boolean seenByDealer);

    long countByContractIdAndSeenByDealerFalse(String contractId);

    @Query("""
            select distinct e.contractId, e.fieldKey
            from ContractChangeEvent e
            where e.seenByDealer = false
              and e.contractId is not null
              and e.fieldKey is not null
            """)
    List<Object[]> findDistinctUnseenContractFieldKeys();
}
