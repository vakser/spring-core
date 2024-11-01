package com.epam.learn.springcore.controller;

import com.epam.learn.springcore.config.CustomAuthenticationProvider;
import com.epam.learn.springcore.dao.UserRepository;
import com.epam.learn.springcore.dto.AuthRequest;
import com.epam.learn.springcore.dto.AuthResponse;
import com.epam.learn.springcore.dto.ChangePasswordRequest;
import com.epam.learn.springcore.entity.User;
import com.epam.learn.springcore.jwt.JwtTokenUtil;
import com.epam.learn.springcore.service.CustomUserDetailsService;
import com.epam.learn.springcore.service.TokenBlacklistService;
import com.epam.learn.springcore.service.UserService;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "REST APIs for Login and Change Password")
public class LoginController {
    private final UserService userService;
    private final Counter hitCounter;
    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserRepository userRepository;

    public LoginController(UserService userService, MeterRegistry meterRegistry,
                           CustomAuthenticationProvider customAuthenticationProvider, JwtTokenUtil jwtTokenUtil,
                           CustomUserDetailsService userDetailsService, TokenBlacklistService tokenBlacklistService, UserRepository userRepository) {
        this.userService = userService;
        this.hitCounter = Counter.builder("hit_login_counter")
                .description("Number of login hits")
                .register(meterRegistry);
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Login User", description = "Is used to login user to application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully authenticated"),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content)
    })
    @Timed(value = "custom_api_timing", description = "Time taken to process /api/login")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        hitCounter.increment();
        customAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(),
                authRequest.getPassword()));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return new ResponseEntity<>(new AuthResponse(token, authRequest.getUsername()), HttpStatus.OK);
    }

    @Operation(summary = "Change Password", description = "Is used to change password for user in database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password successfully changed"),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content)
    })
    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        Optional<User> user = userRepository.findByUsername(changePasswordRequest.getUsername());
        if (user.isPresent() && user.get().getPassword().equals(changePasswordRequest.getOldPassword())) {
            userService.changePassword(changePasswordRequest.getUsername(), changePasswordRequest.getNewPassword());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Logout User", description = "Is used to sign out user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User successfully logged out"),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content)
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        String jwtToken = extractJwtTokenFromRequest(request);
        if (jwtToken != null) {
            tokenBlacklistService.addTokenToBlacklist(jwtToken);
        }
    }

    private String extractJwtTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
