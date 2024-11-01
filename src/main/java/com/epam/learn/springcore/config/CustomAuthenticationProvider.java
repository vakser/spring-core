package com.epam.learn.springcore.config;

import com.epam.learn.springcore.service.BruteForceProtectionService;
import com.epam.learn.springcore.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final BruteForceProtectionService bruteForceProtectionService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        // Check if the user is blocked due to too many failed login attempts
        if (bruteForceProtectionService.isBlocked(username)) {
            throw new BadCredentialsException("You have been temporarily locked due to too many failed login attempts.");
        }
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        // Verify user credentials
        if (userDetails == null || !passwordEncoder.matches(password, userDetails.getPassword())) {
            bruteForceProtectionService.loginFailed(username); // Record failed login attempt
            throw new BadCredentialsException("Invalid username or password.");
        }

        bruteForceProtectionService.loginSucceeded(username); // Record successful login
        return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
