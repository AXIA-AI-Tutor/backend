package com.ax.avatarcoach.global.security.config;

import com.ax.avatarcoach.global.exception.ErrorCode;
import com.ax.avatarcoach.global.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class InternalApiKeyAuthFilter extends OncePerRequestFilter {

    private static final String INTERNAL_API_PREFIX = "/internal/";
    private static final String API_KEY_HEADER = "X-Internal-Api-Key";

    private final InternalApiProperties internalApiProperties;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith(INTERNAL_API_PREFIX);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String requestApiKey = request.getHeader(API_KEY_HEADER);
        if (requestApiKey == null || !requestApiKey.equals(internalApiProperties.apiKey())) {
            writeUnauthorizedResponse(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeUnauthorizedResponse(HttpServletResponse response) throws IOException {
        ErrorCode unauthorized = ErrorCode.UNAUTHORIZED;
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(
            ErrorResponse.of(unauthorized.getCode(), unauthorized.getMessage())
        ));
    }
}
