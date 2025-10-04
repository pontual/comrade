package com.pontual_telemetria.pontual_monitor_api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "security.jwt.cookie")
@Getter @Setter
public class JwtCookieProperties {
    private boolean secure;
    private String sameSite;
}
