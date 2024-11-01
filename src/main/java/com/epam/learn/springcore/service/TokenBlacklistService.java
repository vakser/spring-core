package com.epam.learn.springcore.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlacklistService {
    private final Set<String> tokenBlacklist = new HashSet<>();

    public void addTokenToBlacklist(String token) {
        tokenBlacklist.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }
}
