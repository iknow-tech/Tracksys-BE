package com.iknow.iflowtracksysproxy.service;

import com.iknow.iflowtracksysproxy.cache.CustomerContractCache;
import com.iknow.iflowtracksysproxy.integration.miles.MilesApi;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.CustomerContractResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MilesContractSyncService {

    private final MilesApi milesApi;
    private final CustomerContractCache cache;

    public void syncFromMiles(String trigger) {

        log.info("🔄 Miles sync started (trigger={})", trigger);

        List<CustomerContractResponse> contracts =
                milesApi.getCustomerContracts();

        if (contracts == null || contracts.isEmpty()) {
            log.warn("⚠ Miles returned empty data (trigger={})", trigger);
            return;
        }

        cache.update(contracts);

        log.info("✅ Miles sync finished (trigger={}, count={}, updatedAt={})",
                trigger,
                contracts.size(),
                cache.getLastUpdatedAt());
    }
}
