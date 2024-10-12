package com.epam.learn.springcore.controller;

import com.epam.learn.springcore.dto.ChangePasswordRequest;
import com.epam.learn.springcore.facade.AuthenticationFacade;
import com.epam.learn.springcore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class LoginController {
    private final UserService userService;
    private final AuthenticationFacade authenticationFacade;

    @Operation(summary = "Login User", description = "Is used to login user to application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully authenticated"),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Void> login(@RequestHeader HttpHeaders headers) {
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
