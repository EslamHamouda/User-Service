package com.ecommerce.userservice.security;

import com.ecommerce.userservice.entity.RoleEntity;
import com.ecommerce.userservice.entity.UserEntity;
import com.ecommerce.userservice.enums.TokenType;
import com.ecommerce.userservice.exception.BadCredentialsException;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import javax.crypto.SecretKey;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.accessToken.secret}")
    private String accessSecret;

    @Value("${jwt.refreshToken.secret}")
    private String refreshSecret;

    @Value("${jwt.passwordResetToken.secret}")
    private String passwordResetSecret;

    @Value("${jwt.accessToken.expiration}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refreshToken.expiration}")
    private long refreshTokenExpirationMs;

    @Value("${jwt.passwordResetToken.expiration}")
    private long passwordResetTokenExpirationMs;

    private SecretKey getKeyForType(TokenType type) {
        String secret = switch (type) {
            case ACCESS -> accessSecret;
            case REFRESH -> refreshSecret;
            case PASSWORD_RESET -> passwordResetSecret;
        };
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    private long getExpirationForType(TokenType type) {
        return switch (type) {
            case ACCESS -> accessTokenExpirationMs;
            case REFRESH -> refreshTokenExpirationMs;
            case PASSWORD_RESET -> passwordResetTokenExpirationMs;
        };
    }

    public String generateAccessToken(UserEntity user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles().stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toList()));
        return buildToken(claims, user.getUsername(), TokenType.ACCESS);
    }

    public String generateRefreshToken(String subject) {
        return buildToken(new HashMap<>(), subject, TokenType.REFRESH);
    }

    public String generatePasswordResetToken(String email) {
        return buildToken(new HashMap<>(), email, TokenType.PASSWORD_RESET);
    }


    private String buildToken(Map<String, Object> claims, String subject, TokenType type) {
        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + getExpirationForType(type)))
                .signWith(getKeyForType(type))
                .compact();
    }

    public String extractUsername(String token, TokenType type) {
        return extractAllClaims(token, type).getSubject();
    }

    public Date extractExpiration(String token, TokenType type) {
        return extractAllClaims(token, type).getExpiration();
    }

    public boolean validateToken(String token, TokenType type) {
        try {
            extractAllClaims(token, type); // Will throw if invalid
            return true;
        } catch (JwtException e) {
            throw new BadCredentialsException(e.getMessage());
        }
    }

    public boolean isExpired(String token, TokenType type) {
        try {
            return extractAllClaims(token, type).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    private Claims extractAllClaims(String token, TokenType type) {
        return Jwts.parser()
                .verifyWith(getKeyForType(type))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
