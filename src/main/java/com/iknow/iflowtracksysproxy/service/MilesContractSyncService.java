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

    public List<CustomerContractResponse> refreshCacheFromMiles(String trigger) {

        log.info("Miles cache refresh started (trigger={})", trigger);
        logHeapUsage("refreshCacheFromMiles:start", null);

        List<CustomerContractResponse> contracts = milesApi.getCustomerContracts();
        logHeapUsage("refreshCacheFromMiles:after-miles-fetch", contracts == null ? null : contracts.size());

        if (contracts == null || contracts.isEmpty()) {
            log.warn("Miles returned empty data during cache refresh (trigger={})", trigger);
            return List.of();
        }

        cache.update(contracts);
        logHeapUsage("refreshCacheFromMiles:after-cache-update", contracts.size());

        log.info("Miles cache refresh finished (trigger={}, count={}, updatedAt={})",
                trigger,
                contracts.size(),
                cache.getLastUpdatedAt());

        return contracts;
    }

    public void syncFromMiles(String trigger) {

        log.info("Miles sync started (trigger={})", trigger);
        logHeapUsage("syncFromMiles:start", null);

        List<CustomerContractResponse> contracts =
                milesApi.getCustomerContracts();
        logHeapUsage("syncFromMiles:after-miles-fetch", contracts == null ? null : contracts.size());

        if (contracts == null || contracts.isEmpty()) {
            log.warn("Miles returned empty data (trigger={})", trigger);
            return;
        }

        List<CustomerContractResponse> oldContracts = cache.get();
        logHeapUsage("syncFromMiles:after-cache-get", oldContracts == null ? 0 : oldContracts.size());

        Map<String, CustomerContractResponse> oldMap =
                (oldContracts == null ? List.<CustomerContractResponse>of() : oldContracts)
                        .stream()
                        .filter(c -> c.getId() != null)
                        .collect(Collectors.toMap(
                                CustomerContractResponse::getId,
                                c -> c,
                                (a, b) -> a
                        ));
        logHeapUsage("syncFromMiles:after-old-map-build", oldMap.size());

        String batchId = UUID.randomUUID().toString();

        List<ContractChangeEvent> events = new ArrayList<>();

        for (CustomerContractResponse newC : contracts) {
            if (newC.getId() == null) continue;

            CustomerContractResponse oldC = oldMap.get(newC.getId());
            if (oldC == null) continue;

            events.addAll(contractMilesChangeDetector.detectChanges(oldC, newC, batchId));
        }
        logHeapUsage("syncFromMiles:after-change-detect", events.size());

        if (!events.isEmpty()) {
            contractChangeEventRepository.saveAll(events);
            log.info("Change events created (trigger={}, count={})", trigger, events.size());
            logHeapUsage("syncFromMiles:after-save-events", events.size());
        }

        cache.update(contracts);
        logHeapUsage("syncFromMiles:after-cache-update", contracts.size());

        log.info("Miles sync finished (trigger={}, count={}, updatedAt={})",
                trigger,
                contracts.size(),
                cache.getLastUpdatedAt());
    }

    private void logHeapUsage(String phase, Integer itemCount) {
        Runtime runtime = Runtime.getRuntime();
        long usedMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long totalMb = runtime.totalMemory() / (1024 * 1024);
        long maxMb = runtime.maxMemory() / (1024 * 1024);

        if (itemCount == null) {
            log.info("HEAP phase={} usedMb={} totalMb={} maxMb={}", phase, usedMb, totalMb, maxMb);
            return;
        }

        log.info("HEAP phase={} items={} usedMb={} totalMb={} maxMb={}", phase, itemCount, usedMb, totalMb, maxMb);
    }
}
