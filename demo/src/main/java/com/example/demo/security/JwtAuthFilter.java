package com.example.demo.security;

import com.example.demo.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT Authentication Filter that intercepts HTTP requests to validate JWT tokens.
 * This filter extends OncePerRequestFilter to ensure it's executed only once per request.
 * 
 * Responsibilities:
 * - Skip authentication for public endpoints (auth, swagger, etc.)
 * - Extract and validate JWT tokens from Authorization headers
 * - Set Spring Security authentication context for valid tokens
 * - Handle authentication errors gracefully
 * 
 * Filter Flow:
 * 1. Check if the requested path is public (no auth required)
 * 2. Extract JWT token from "Authorization: Bearer <token>" header
 * 3. Validate token and extract user information
 * 4. Set authentication in Spring Security context
 * 5. Continue with the filter chain or return error response
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    /** JWT utility service for token operations (validation, extraction) */
    private final JwtUtil jwtUtil;

    /** 
     * List of public endpoints that don't require authentication.
     * These paths will bypass JWT validation entirely.
     */
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/",        // Authentication endpoints (login, register)
            "/swagger-ui",       // Swagger UI documentation
            "/v3/api-docs",      // OpenAPI 3.0 documentation
            "/swagger-resources", // Swagger resources
            "/webjars"           // Static web resources
    );

    /**
     * Main filter method that processes each HTTP request for JWT authentication.
     * This method is called once per request to validate JWT tokens and set authentication context.
     * 
     * @param request The HTTP request containing potential JWT token
     * @param response The HTTP response for sending error messages
     * @param filterChain The filter chain to continue processing
     * @throws ServletException If servlet processing fails
     * @throws IOException If I/O operations fail
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // ================================
        // STEP 1: Check if path is public (no authentication needed)
        // ================================
        String path = request.getRequestURI();
        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            // Skip authentication for public endpoints (login, swagger, etc.)
            log.debug("Skipping JWT authentication for public path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        // ================================
        // STEP 2: Extract JWT token from Authorization header
        // ================================
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                // Remove "Bearer " prefix (7 characters) to get the actual JWT token
                String jwt = authHeader.substring(7);
                log.debug("JWT token extracted from Authorization header");
                
                // Extract user email from JWT token subject
                String userEmail = jwtUtil.extractEmail(jwt);

                // ================================
                // STEP 3: Validate token and set authentication
                // ================================
                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Check if token is valid (not expired, correct signature)
                    if (jwtUtil.validateToken(jwt)) {
                        // Extract user role from JWT token custom claims
                        String role = jwtUtil.extractRole(jwt);

                        // Create Spring Security authentication object with user details
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userEmail,           // Principal (user identifier)
                                        null,               // Credentials (not needed for JWT)
                                        List.of(new SimpleGrantedAuthority(role))  // Authorities/roles
                                );

                        // Set authentication in Spring Security context for this request
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.info("Successfully authenticated user: {} with role: {}", userEmail, role);
                    } else {
                        log.warn("Invalid JWT token for user: {}", userEmail);
                    }
                }
            } catch (Exception e) {
                // ================================
                // STEP 4: Handle JWT validation errors
                // ================================
                log.error("JWT authentication failed for path {}: {}", path, e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return; // Stop processing and return 401 Unauthorized response
            }
        } else {
            // No Authorization header or doesn't start with "Bearer "
            log.debug("No valid Authorization header found for protected path: {}", path);
        }

        // ================================
        // STEP 5: Continue with the request
        // ================================
        // Pass request to next filter in the chain (or to the controller if last filter)
        filterChain.doFilter(request, response);
    }
}
