package com.pontual_telemetria.pontual_monitor_api.domain.listener;

import com.pontual_telemetria.pontual_monitor_api.config.RefreshProperties;
import com.pontual_telemetria.pontual_monitor_api.domain.event.RefreshNeededEvent;
import com.pontual_telemetria.pontual_monitor_api.domain.service.MaterializedViewRefreshDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class MVRefreshEventListener {

    private final TaskScheduler scheduler;
    private final MaterializedViewRefreshDomainService service;
    private final RefreshProperties refreshProperties;

    private final Map<Long, ScheduledFuture<?>> timers = new ConcurrentHashMap<>();

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAfterCommit(RefreshNeededEvent event){

        if(!refreshProperties.isEnabled()) return;

        Long location = event.locationId() == null ? 0L : event.locationId();
        Optional.ofNullable(timers.remove(location)).ifPresent(f -> f.cancel(false));

        int delay = Math.max(refreshProperties.getDebounceSeconds(), 1);
        ScheduledFuture<?> f = scheduler.schedule(() -> {
            try { service.refreshAll(location); }
            finally { timers.remove(location); }
        }, Instant.now().plusSeconds(delay));

        timers.put(location, f);
     }
}
