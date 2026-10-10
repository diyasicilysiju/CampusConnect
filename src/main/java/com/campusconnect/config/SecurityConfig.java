package com.campusconnect.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authorizeHttpRequests(auth -> auth
                // Static resources and HTML pages
                .requestMatchers(
                    "/",
                    "/*.html",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/favicon.ico"
                ).permitAll()

                // Public Auth APIs
                .requestMatchers(
                    "/api/auth/register",
                    "/api/auth/login",
                    "/api/auth/logout",
                    "/api/auth/me"
                ).permitAll()

                // Public Event viewing APIs
                .requestMatchers(HttpMethod.GET, "/api/events", "/api/events/*").permitAll()

                // Test reset API (allowed for automated Selenium test resetting)
                .requestMatchers(HttpMethod.POST, "/api/data/reset").permitAll()

                // Admin-only endpoints
                .requestMatchers(HttpMethod.POST, "/api/events").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/events/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/events/*").hasRole("ADMIN")
                .requestMatchers("/api/dashboard/**").hasRole("ADMIN")
                .requestMatchers("/api/registrations/event/*/students").hasRole("ADMIN")

                // Student registration endpoints
                .requestMatchers(HttpMethod.POST, "/api/registrations/event/*").hasRole("STUDENT")
                .requestMatchers(HttpMethod.PUT, "/api/registrations/*/cancel").hasRole("STUDENT")
                .requestMatchers(HttpMethod.GET, "/api/registrations/my").hasRole("STUDENT")

                // Any other request requires authentication
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\":false,\"message\":\"Authentication required. Please log in.\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\":false,\"message\":\"Access denied: Admin privileges required.\"}");
                })
            );

        return http.build();
    }
}
