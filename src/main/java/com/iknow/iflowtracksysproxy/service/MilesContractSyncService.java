package com.iknow.iflowtracksysproxy.service;

import com.iknow.iflowtracksysproxy.cache.CustomerContractCache;
import com.iknow.iflowtracksysproxy.entity.ContractChangeEvent;
import com.iknow.iflowtracksysproxy.integration.miles.MilesApi;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.CustomerContractResponse;
import com.iknow.iflowtracksysproxy.respository.ContractChangeEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MilesContractSyncService {

    private final MilesApi milesApi;
    private final CustomerContractCache cache;
    private final ContractChangeEventRepository contractChangeEventRepository;
    private final ContractMilesChangeDetector contractMilesChangeDetector;

    public void syncFromMiles(String trigger) {

        log.info("Miles sync started (trigger={})", trigger);

        List<CustomerContractResponse> contracts =
                milesApi.getCustomerContracts();

        if (contracts == null || contracts.isEmpty()) {
            log.warn("⚠ Miles returned empty data (trigger={})", trigger);
            return;
        }

        List<CustomerContractResponse> oldContracts = cache.get();

        Map<String, CustomerContractResponse> oldMap =
                (oldContracts == null ? List.<CustomerContractResponse>of() : oldContracts)
                        .stream()
                        .filter(c -> c.getId() != null)
                        .collect(Collectors.toMap(
                                CustomerContractResponse::getId,
                                c -> c,
                                (a, b) -> a
                        ));

        String batchId = UUID.randomUUID().toString();

        List<ContractChangeEvent> events = new ArrayList<>();

        for (CustomerContractResponse newC : contracts) {
            if (newC.getId() == null) continue;

            CustomerContractResponse oldC = oldMap.get(newC.getId());
            if (oldC == null) continue;

            events.addAll(contractMilesChangeDetector.detectChanges(oldC, newC, batchId));
        }

        if (!events.isEmpty()) {
            contractChangeEventRepository.saveAll(events);
            log.info("Change events created (trigger={}, count={})", trigger, events.size());
        }

        cache.update(contracts);

        log.info("Miles sync finished (trigger={}, count={}, updatedAt={})",
                trigger,
                contracts.size(),
                cache.getLastUpdatedAt());
    }
}
