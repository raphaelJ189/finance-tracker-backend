package com.financetracker.security;

import com.financetracker.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    // Convert the secret string into a cryptographic key
    // Called internally every time we need to sign or verify
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                jwtProperties.getSecret()
                        .getBytes(StandardCharsets.UTF_8)
        );
    }

    // Called once when user logs in successfully
    // Builds and returns a signed JWT token
    public String generateToken(Long userId, String email, String role) {

        Date now = new Date();
        Date expiration = new Date(
                now.getTime()
                        + (long) jwtProperties.getExpirationMinutes() * 60 * 1000
        );

        log.debug("Generating token for userId: {}", userId);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                // subject: where do this token belong to
                // we use userId as the subject
                .claim("email", email)
                // custom claim: user's email
                .claim("role", role)
                // custom claim: user's role (USER or ADMIN)
                .issuedAt(now)
                // when this token was created
                .expiration(expiration)
                // when this token expires
                .signWith(getSigningKey())
                // sign with our secret key
                // this creates the signature part of the JWT
                .compact();
        // build the final token string
    }

    // Called on every request that has a token
    // Returns true if token is not expired
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    // checks the signature matches our secret key
                    .build()
                    .parseSignedClaims(token);
            // also checks expiration automatically
            return true;
        } catch (JwtException exception) {
            log.warn("Invalid JWT token: {}", exception.getMessage());
            return false;
        }
    }

    // Extract user id from token
    // Called after validation to know who is making the request
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    // Extract role from token
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    // Extract all claims from token
    // Private helper used by extractUserId and extractRole
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}