package com.iknow.iflowtracksysproxy.scheduler;

import com.iknow.iflowtracksysproxy.service.MilesContractSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MilesContractScheduler {

    private final MilesContractSyncService syncService;

    @Scheduled(cron = "0 */2 * * * *")
    public void run() {
        log.info("⏰ Scheduler başlatıldı...");
        syncService.syncFromMiles("SCHEDULER_5_MIN");
    }

}
