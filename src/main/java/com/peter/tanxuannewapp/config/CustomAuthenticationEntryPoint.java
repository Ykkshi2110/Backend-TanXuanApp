package com.peter.tanxuannewapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peter.tanxuannewapp.domain.resposne.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();

    private final ObjectMapper objectMapper;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        this.delegate.commence(request, response, authException);
        response.setContentType("application/json;charset=utf-8");

        ApiResponse<Object> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        String errorMessage = Optional.ofNullable(authException.getCause()).map(Throwable::getMessage).orElse(authException.getMessage());
        apiResponse.setError(errorMessage);
        apiResponse.setMessage("Invalid Token (expired, incorrect format...)");

        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}
