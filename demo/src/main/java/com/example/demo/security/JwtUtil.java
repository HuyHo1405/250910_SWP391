package com.example.demo.security;

import com.example.demo.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT (JSON Web Token) utility class for handling token generation, validation, and extraction.
 * This component provides methods to create secure tokens for user authentication and authorization.
 *
 * Features:
 * - Access Token: 15 minutes expiration (for API requests)
 * - Refresh Token: 7 days expiration (for obtaining new access tokens)
 * - Token validation and expiration checking
 * - Extraction of user email and role from tokens
 * - Secure token signing with HS256 algorithm
 */
@Component
public class JwtUtil {

    /** Secret key used for signing JWT tokens with HS256 algorithm */
    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /** Access token expiration time: 15 minutes = 900000 ms */
    private final long accessTokenExpiration = 900000 * 10; // 15 * 10 minutes

    /** Refresh token expiration time: 7 days = 604800000 ms */
    private final long refreshTokenExpiration = 604800000L; // 7 days

    /**
     * Generates an ACCESS TOKEN for the given user.
     * The token includes the user's role as a custom claim and email as the subject.
     * This token is SHORT-LIVED (15 minutes) for security purposes.
     *
     * @param user The user entity containing email and role information
     * @return A signed JWT access token string valid for 15 minutes
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().getName());
        claims.put("type", "access"); // Đánh dấu đây là access token

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmailAddress())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(key)
                .compact();
    }

    /**
     * Generates a REFRESH TOKEN with a longer expiration time than the access token.
     * Refresh tokens are used to obtain new access tokens without requiring re-authentication.
     * This token is LONG-LIVED (7 days).
     *
     * @param user The user entity for whom the refresh token is being generated
     * @return A signed JWT refresh token string valid for 7 days
     */
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh"); // Đánh dấu đây là refresh token

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmailAddress())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(key)
                .compact();
    }

    /**
     * Extracts the email address (subject) from a JWT token.
     *
     * @param token The JWT token string
     * @return The email address stored as the token's subject
     */
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Extracts the user role from a JWT token's custom claims.
     *
     * @param token The JWT token string
     * @return The role name stored in the token's claims
     */
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    /**
     * Extracts the user ID from a JWT token's custom claims.
     *
     * @param token The JWT token string
     * @return The user ID stored in the token's claims
     */
    public Long extractUserId(String token) {
        return extractClaims(token).get("userId", Long.class);
    }

    /**
     * Validates a JWT token by checking its signature and expiration time.
     *
     * @param token The JWT token string to validate
     * @return true if the token is valid and not expired, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extracts the expiration date from a JWT token.
     * Useful for blacklisting tokens until they naturally expire.
     *
     * @param token The JWT token string
     * @return LocalDateTime representing when the token expires
     */
    public LocalDateTime getExpirationDateFromToken(String token) {
        Date expirationDate = extractClaims(token).getExpiration();
        return expirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * Extracts all claims from a JWT token.
     * This is a private helper method that parses and validates the token signature.
     *
     * @param token The JWT token string
     * @return Claims object containing all token data
     * @throws Exception if token is invalid or signature verification fails
     */
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Returns the ACCESS TOKEN expiration time in seconds.
     * This is useful for clients to know when to refresh the token.
     *
     * @return The number of seconds until the access token expires (900 seconds = 15 minutes)
     */
    public long getExpirationInSeconds() {
        return accessTokenExpiration / 1000;
    }

    /**
     * Returns the REFRESH TOKEN expiration time in seconds.
     *
     * @return The number of seconds until the refresh token expires (604800 seconds = 7 days)
     */
    public long getRefreshTokenExpirationInSeconds() {
        return refreshTokenExpiration / 1000;
    }
}