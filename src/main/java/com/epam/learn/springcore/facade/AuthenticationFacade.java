package com.epam.learn.springcore.facade;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AuthenticationFacade {
    public String extractAuthToken(HttpHeaders headers) {
        if (headers.containsKey(HttpHeaders.AUTHORIZATION)) {
            return Objects.requireNonNull(headers.get(HttpHeaders.AUTHORIZATION)).getFirst().substring(7);
        }
        return null;  // No valid authorization header
    }
}
