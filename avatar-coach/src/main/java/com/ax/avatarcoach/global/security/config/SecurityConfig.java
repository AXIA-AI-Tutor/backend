package com.ax.avatarcoach.global.security.config;

import com.ax.avatarcoach.global.exception.ErrorCode;
import com.ax.avatarcoach.global.response.ErrorResponse;
import com.ax.avatarcoach.global.security.oauth.CustomOAuth2UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final ObjectMapper objectMapper;

    @Value("${app.oauth2.success-redirect-url}")
    private String successRedirectUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/health",
                    "/oauth2/**",
                    "/login/**",
                    "/error",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()
                .requestMatchers(
                    "/api/users/me",
                    "/api/sessions/**",
                    "/api/feedbacks/**"
                ).authenticated()
                .anyRequest().permitAll()
            )
            .exceptionHandling(ex -> ex
                .defaultAuthenticationEntryPointFor((request, response, authException) -> {
                    ErrorCode unauthorized = ErrorCode.UNAUTHORIZED;
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(objectMapper.writeValueAsString(
                        ErrorResponse.of(unauthorized.getCode(), unauthorized.getMessage())
                    ));
                }, request -> request.getRequestURI().startsWith("/api/"))
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
                .defaultSuccessUrl(successRedirectUrl, true)
            );

        return http.build();
    }
}
