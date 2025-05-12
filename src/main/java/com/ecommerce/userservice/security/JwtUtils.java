package com.ecommerce.userservice.security;

import com.ecommerce.userservice.entity.UserEntity;
import com.ecommerce.userservice.exception.BadCredentialsException;
import com.ecommerce.userservice.exception.ValidationException;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import javax.crypto.SecretKey;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.accessToken.expiration}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refreshToken.expiration}")
    private long refreshTokenExpirationMs;

    @Value("${jwt.passwordResetToken.expiration}")
    private long passwordResetTokenExpirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Authentication authentication) {
        UserEntity userPrincipal = (UserEntity) authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenType", "access");
        claims.put("role", userPrincipal.getRole().getName().name());
        return tokenBuilder(claims, userPrincipal.getUsername(), accessTokenExpirationMs);
    }

    public String generateAccessToken(String subject) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenType", "access");
        return tokenBuilder(claims, subject, accessTokenExpirationMs);
    }

    public String generateRefreshToken(String subject) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenType", "refresh");
        return tokenBuilder(claims, subject, refreshTokenExpirationMs);
    }

    public String generatePasswordResetToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenType", "passwordReset");
        return tokenBuilder(claims, email, passwordResetTokenExpirationMs);
    }

    private String tokenBuilder(Map<String, Object> claims, String subject, Long expiration) {
        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (JwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw new BadCredentialsException("Invalid JWT token: {}" + e.getMessage());
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return "refresh".equals(claims.get("tokenType"));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = extractAllClaims(token);

            return claims.getExpiration().before(new Date());
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isPasswordResetToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return "passwordReset".equals(claims.get("tokenType"));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return "access".equals(claims.get("tokenType"));
        } catch (Exception e) {
            return false;
        }
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
