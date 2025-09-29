package com.example.demo.config;

import com.example.demo.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security Configuration class that defines the security settings for the application.
 * This configuration enables JWT-based authentication with role-based access control.
 * 
 * Key Features:
 * - JWT token-based authentication (stateless)
 * - Role-based authorization (CUSTOMER, TECHNICIAN, STAFF, ADMIN)
 * - CORS configuration for cross-origin requests
 * - BCrypt password encoding
 * - Public endpoints for authentication and documentation
 * 
 * Security Architecture:
 * - Disables CSRF (not needed for stateless JWT)
 * - Enables CORS for frontend integration
 * - Uses custom JWT filter for token validation
 * - Implements hierarchical role-based access control
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /** Custom JWT authentication filter for token validation and user authentication */
    private final JwtAuthFilter jwtAuthFilter;

    /**
     * Configures the password encoder bean for secure password hashing.
     * Uses BCrypt algorithm which is considered secure for password storage.
     * 
     * @return BCryptPasswordEncoder instance for password hashing and verification
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures CORS (Cross-Origin Resource Sharing) settings for the application.
     * This allows frontend applications running on different domains to access the API.
     * 
     * Current Configuration:
     * - Allows all origins (suitable for development)
     * - Supports standard HTTP methods (GET, POST, PUT, DELETE, OPTIONS)
     * - Allows all headers
     * - Enables credentials (cookies, authorization headers)
     * 
     * @return CorsConfigurationSource with permissive CORS settings for development
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*")); // Development: Allow all origins
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*")); // Allow all headers including Authorization
        config.setAllowCredentials(true); // Allow cookies and authorization headers

        // Apply CORS configuration to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Configures the main security filter chain that defines authentication and authorization rules.
     * This method sets up the core security behavior of the application.
     * 
     * Security Configuration:
     * 1. Disables CSRF protection (not needed for stateless JWT authentication)
     * 2. Enables CORS using the configured CORS bean
     * 3. Sets session management to STATELESS (no server-side sessions)
     * 4. Defines public endpoints that don't require authentication
     * 5. Implements role-based access control for protected endpoints
     * 6. Adds custom JWT filter before Spring's default authentication filter
     * 
     * @param http HttpSecurity object for configuring security settings
     * @return SecurityFilterChain with complete security configuration
     * @throws Exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection (not needed for stateless JWT authentication)
                .csrf(csrf -> csrf.disable())
                
                // Enable CORS using the configured CORS bean
                .cors(cors -> {}) // Uses corsConfigurationSource bean automatically
                
                // Configure stateless session management (no server-side sessions)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // Configure authorization rules for different endpoints
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no authentication required
                        .requestMatchers(
                                "/api/auth/**",        // Authentication endpoints (login, register)
                                "/swagger-ui/**",      // Swagger UI documentation
                                "/v3/api-docs/**",     // OpenAPI 3.0 specification
                                "/swagger-resources/**", // Swagger resources
                                "/webjars/**"          // Static web resources (CSS, JS)
                        ).permitAll()

                        // Role-based access control for protected endpoints
                        
                        // Customer endpoints - accessible only to users with CUSTOMER role
                        .requestMatchers("/api/customer/**").hasRole("CUSTOMER")

                        // Technician endpoints - accessible only to users with TECHNICIAN role
                        .requestMatchers("/api/technician/**").hasRole("TECHNICIAN")

                        // Staff endpoints - accessible only to users with STAFF role
                        .requestMatchers("/api/staff/**").hasRole("STAFF")

                        // Admin endpoints - accessible only to users with ADMIN role
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        
                        // All other requests require authentication (but no specific role)
                        .anyRequest().authenticated()
                )
                // Add custom JWT filter before Spring's default authentication filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}