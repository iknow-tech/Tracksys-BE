package com.iknow.iflowtracksysproxy.scheduler;

import com.iknow.iflowtracksysproxy.service.MilesContractSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MilesContractScheduler {

    private final MilesContractSyncService syncService;

    public void run() {
        log.info("Miles contract scheduler is disabled");
        syncService.syncFromMiles("SCHEDULER_1_HOUR");
    }

}
