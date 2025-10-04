package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.config.RefreshProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaterializedViewRefreshDomainService {

    private final JdbcTemplate jdbc;
    private final RefreshProperties props;

    private final ConcurrentHashMap<Long, Boolean> running = new ConcurrentHashMap<>();

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void refreshAll(Long locationId){

        if(!tryLock(locationId)){
            log.info("[MV] lock não adquirido (locationId={}) — deve estar sendo executada em outra instância", locationId);
            return;
        }

        try {
            jdbc.execute("REFRESH MATERIALIZED VIEW sch_view.mv_daily_agg");
            jdbc.execute("REFRESH MATERIALIZED VIEW sch_view.mv_monthly_agg");

            log.info("[MV] refresh concluído (locationId={})", locationId);
        } catch (DataAccessException e) {
            log.error("[MV] erro no refresh (locationId={}): {}", locationId, e.getMessage(), e);
        } finally {
            unlock(locationId);
        }
    }

    public void awaitIfRefreshing(Long locationId, long timeoutMs) {
        Long keyLoc = locationId == null ? 0L : locationId;
        long end = System.currentTimeMillis() + timeoutMs;
        while (Boolean.TRUE.equals(running.get(keyLoc)) && System.currentTimeMillis() < end) {
            try { Thread.sleep(150); } catch (InterruptedException ignored) {}
        }
    }

    private boolean tryLock(Long locationId) {
        long key = props.getLockBaseKey() + (locationId == null ? 0L : locationId);
        Boolean got = jdbc.queryForObject("select pg_try_advisory_lock(?)", Boolean.class, key);
        return Boolean.TRUE.equals(got);
    }

    private void unlock(Long locationId) {
        long key = props.getLockBaseKey() + (locationId == null ? 0L : locationId);
        jdbc.queryForObject("select pg_advisory_unlock(?)", Boolean.class, key);
    }
}
