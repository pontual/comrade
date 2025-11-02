package com.pontual_telemetria.pontual_monitor_api.infrastructure.util;

import com.pontual_telemetria.pontual_monitor_api.domain.model.configuration.Function;
import com.pontual_telemetria.pontual_monitor_api.domain.service.ConfigurationDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ApplicationUtils {

    private final ConfigurationDomainService configurationDomainService;

    public boolean enableCapTo24() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            boolean isUserRole = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(a -> "USER".equalsIgnoreCase(a) || "ROLE_USER".equalsIgnoreCase(a));

            if (isUserRole) {
                return true;
            }
        }

        List<Function> functions = Optional.ofNullable(configurationDomainService.functions())
                .orElse(List.of());

        return functions.stream()
                .filter(f -> Constants.CAP_TO_24H.equalsIgnoreCase(f.getName()))
                .map(f -> Boolean.TRUE.equals(f.getEnabled()))
                .findFirst()
                .orElse(false);
    }
}
