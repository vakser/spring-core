package com.epam.learn.springcore.controller;

import com.epam.learn.springcore.dto.ChangePasswordRequest;
import com.epam.learn.springcore.facade.AuthenticationFacade;
import com.epam.learn.springcore.service.UserService;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/login")
@Tag(name = "REST APIs for Login and Change Password")
public class LoginController {
    private final UserService userService;
    private final AuthenticationFacade authenticationFacade;
    private final Counter hitCounter;

    public LoginController(UserService userService, AuthenticationFacade authenticationFacade,
                           MeterRegistry meterRegistry) {
        this.userService = userService;
        this.authenticationFacade = authenticationFacade;
        this.hitCounter = Counter.builder("hit_login_counter")
                .description("Number of login hits")
                .register(meterRegistry);
    }

    @Operation(summary = "Login User", description = "Is used to login user to application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully authenticated"),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content)
    })
    @Timed(value = "custom_api_timing", description = "Time taken to process /api/login")
    @GetMapping
    public ResponseEntity<Void> login(@RequestHeader HttpHeaders headers) {
        hitCounter.increment();
        Optional<String[]> token = authenticationFacade.extractAndValidateAuthToken(headers);
        if (token.isPresent()) {
            String username = token.get()[0];
            String password = token.get()[1];
            if (userService.authenticate(username, password)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Change Password", description = "Is used to change password for user in database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password successfully changed"),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content)
    })
    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        if (userService.authenticate(changePasswordRequest.getUsername(), changePasswordRequest.getOldPassword())) {
            userService.changePassword(changePasswordRequest.getUsername(), changePasswordRequest.getNewPassword());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
