package com.pontual_telemetria.pontual_monitor_api.infrastructure.jdbc;

import com.pontual_telemetria.pontual_monitor_api.web.dto.monitoring.dailyoperation.DailyCalculatedItemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class DailyCalculatedJdbcRepository {

    private final NamedParameterJdbcTemplate jdbc;

    private static final String SQL = """
        SELECT
            day,
            mv_daily_hours        AS "mvDailyHours",
            daily_hours_override  AS "dailyHoursOverride",
            override_updated_by   AS "overrideUpdatedBy",
            override_updated_at   AS "overrideUpdatedAt"
        FROM sch_monitoring.vw_daily_calculated_final
        WHERE external_id = :externalId
        ORDER BY day
        """;

    public List<DailyCalculatedItemDTO> findAllByExternalId(Long externalId) {
        return jdbc.query(SQL, Map.of("externalId", externalId), (rs, i) -> new DailyCalculatedItemDTO(
                rs.getObject("day", LocalDate.class),
                rs.getObject("mvDailyHours", BigDecimal.class),
                rs.getObject("dailyHoursOverride", BigDecimal.class),
                rs.getString("overrideUpdatedBy"),
                rs.getObject("overrideUpdatedAt", OffsetDateTime.class)
        ));
    }
}
