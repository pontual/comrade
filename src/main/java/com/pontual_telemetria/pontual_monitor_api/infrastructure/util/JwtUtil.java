package com.pontual_telemetria.pontual_monitor_api.infrastructure.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${security.jwt.secret}")
    private String secretKey;

    @Value("${security.jwt.access-expiration-ms}")
    private long accessTokenExpirationTime;

    @Value("${security.jwt.refresh-expiration-ms}")
    private long refreshTokenExpirationTime;

    public static final String ROLES = "roles";

    private Key getKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateAccessToken(String username, List<String> roles) {
        return Jwts.builder()
                .subject(username)
                .claim(ROLES, roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
                .signWith(getKey())
                .compact();
    }

    public String generateTokenFromRefreshToken(String username, List<String> roles) {
        return Jwts.builder()
                .subject(username)
                .claim(ROLES, roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
                .signWith(getKey())
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public List<String> extractRoles(String token) {
        List<?> rawRoles = getClaims(token).get(ROLES, List.class);
        return rawRoles.stream()
                .map(Object::toString)
                .toList();
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
