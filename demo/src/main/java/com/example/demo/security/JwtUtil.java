package com.example.demo.security;

import com.example.demo.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT (JSON Web Token) utility class for handling token generation, validation, and extraction.
 * This component provides methods to create secure tokens for user authentication and authorization.
 * 
 * Features:
 * - Token generation with user role information
 * - Token validation and expiration checking
 * - Extraction of user email and role from tokens
 * - Secure token signing with HS256 algorithm
 */
@Component
public class JwtUtil {
    
    /** Secret key used for signing JWT tokens with HS256 algorithm */
    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    
    /** JWT token expiration time in milliseconds (24 hours = 86400000 ms) */
    private final long jwtExpiration = 86400000; // 24 hours

    /**
     * Generates a JWT token for the given user.
     * The token includes the user's role as a custom claim and email as the subject.
     * 
     * @param user The user entity containing email and role information
     * @return A signed JWT token string valid for 24 hours
     */
    public String generateToken(User user) {
        // Create a map to store custom claims
        Map<String, Object> claims = new HashMap<>();
        // Add user's role name as a claim for authorization purposes
        claims.put("role", user.getRole().getName());
        // Add user's ID as a claim for user identification
        claims.put("userId", user.getId());

        // Create and return the JWT token with claims and user email as subject
        return createToken(claims, user.getEmailAddress());
    }

    /**
     * Creates a JWT token with the specified claims and subject.
     * This is a private helper method that builds the actual JWT structure.
     * 
     * @param claims Custom claims to be included in the token
     * @param subject The subject of the token (typically user email)
     * @return A compact JWT token string
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)                                                    // Set custom claims
                .setSubject(subject)                                                  // Set subject (user email)
                .setIssuedAt(new Date(System.currentTimeMillis()))                   // Set token creation time
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Set expiration time
                .signWith(key)                                                        // Sign with secret key
                .compact();                                                           // Build compact token string
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
            // Extract claims and check if token is not expired
            Claims claims = extractClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            // Return false if token parsing fails (invalid signature, malformed token, etc.)
            return false;
        }
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
                .setSigningKey(key)        // Set the signing key for verification
                .build()                   // Build the parser
                .parseClaimsJws(token)     // Parse and verify the token
                .getBody();                // Extract the claims body
    }

    /**
     * Generates a refresh token with a longer expiration time than the access token.
     * Refresh tokens are used to obtain new access tokens without requiring re-authentication.
     *
     * @param user The user entity for whom the refresh token is being generated
     * @return A signed JWT refresh token string valid for 7 days
     */
    public String generateRefreshToken(User user) {
        // Refresh token expires in 7 days (7 * 24 * 60 * 60 * 1000 = 604800000 ms)
        return Jwts.builder()
                .setSubject(user.getEmailAddress())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 604800000))
                .signWith(key)
                .compact();
    }

    /**
     * Returns the JWT token expiration time in seconds.
     * This is useful for clients to know when to refresh the token.
     *
     * @return The number of seconds until the token expires
     */
    public long getExpirationInSeconds() {
        return jwtExpiration / 1000; // Convert milliseconds to seconds
    }
}
