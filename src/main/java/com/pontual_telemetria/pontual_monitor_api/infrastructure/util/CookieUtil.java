package com.pontual_telemetria.pontual_monitor_api.infrastructure.util;

import com.pontual_telemetria.pontual_monitor_api.config.JwtCookieProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final JwtCookieProperties jwtCookieProperties;

    public static final String ACCESS_TOKEN_COOKIE = "accessToken";
    public static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    public String getCookieValue(HttpServletRequest request, String cookieName) {
        if(request.getCookies() != null) {
            for(var cookie: request.getCookies()) {
                if(cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        //for Swagger tests
        if(ACCESS_TOKEN_COOKIE.equals(cookieName)){
            String authHeader = request.getHeader("Authorization");
            if(authHeader != null && authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
        }

        return null;
    }

    public ResponseCookie createCookie(String name, String value, Duration duration){
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(jwtCookieProperties.isSecure())
                .sameSite(jwtCookieProperties.getSameSite())
                .path("/")
                .maxAge(duration)
                .build();
    }

    public ResponseCookie createSessionCookie(String name, String value){
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(jwtCookieProperties.isSecure())
                .sameSite(jwtCookieProperties.getSameSite())
                .path("/")
                .build();
    }

    public ResponseCookie expireCookie(String name) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(jwtCookieProperties.isSecure())
                .sameSite(jwtCookieProperties.getSameSite())
                .path("/")
                .maxAge(Duration.ZERO)
                .build();
    }

    public static void attachCookies(HttpServletResponse response, ResponseCookie... cookies){
        for (ResponseCookie cookie : cookies){
            response.addHeader("Set-Cookie", cookie.toString());
        }
    }
}
