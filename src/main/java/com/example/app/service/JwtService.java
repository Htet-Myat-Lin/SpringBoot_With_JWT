package com.example.app.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Service
public class JwtService {
    private static final String SECRET_KEY = "my-super-secret-jwt-key-for-spring-boot-app-12345678901234567890123456789012";
    private static final long ACCESS_TOKEN_EXPIRY = 1000L * 60 * 10; // 10 minutes
    private static final long REFRESH_TOKEN_EXPIRY = 1000L * 60 * 60 * 24 * 10; // 10 days

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY))
                .signWith(this.getKey())
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY))
                .signWith(this.getKey())
                .compact();
    }

    /**
     * Extract username with full validation
     */
    public String extractUsername(String token) throws JwtException {
        Claims claims = getAllClaims(token);
        return claims.getSubject();
    }

    /**
     * Validate token: checks signature and expiration
     */
    public boolean isTokenValid(String token) {
        try {
            Date tokenExpiration = getTokenExpiration(token);

            // Check if token is expired
            if (tokenExpiration.before(new Date())) {
                log.warn("Token has expired at: {}", tokenExpiration);
                return false;
            }

            log.debug("Token is valid");
            return true;
        } catch (JwtException e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract all claims from token (validates signature)
     */
    private Claims getAllClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(this.getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Get token expiration time
     */
    public Date getTokenExpiration(String token) throws JwtException {
        return getAllClaims(token).getExpiration();
    }
}