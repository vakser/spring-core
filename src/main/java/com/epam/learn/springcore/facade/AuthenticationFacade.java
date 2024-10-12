package com.epam.learn.springcore.facade;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class AuthenticationFacade {
    public Optional<String[]> extractAndValidateAuthToken(HttpHeaders headers) {
        if (headers.containsKey(HttpHeaders.AUTHORIZATION)) {
            String token = Objects.requireNonNull(headers.get(HttpHeaders.AUTHORIZATION)).get(0);
            String[] tokenParts = token.split(":");
            if (tokenParts.length == 2) {
                return Optional.of(tokenParts);  // returns [authUsername, authPassword]
            }
        }
        return Optional.empty();  // No valid authorization header
    }
}
