package com.pontual_telemetria.pontual_monitor_api.config;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "refresh.mviews")
public class RefreshProperties {
    private boolean enabled = true;
    private int debounceSeconds = 10;
    private long lockBaseKey = 42000L;
}
