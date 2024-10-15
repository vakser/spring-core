package com.epam.learn.springcore.controller;

import com.epam.learn.springcore.entity.TrainingType;
import com.epam.learn.springcore.facade.AuthenticationFacade;
import com.epam.learn.springcore.service.TrainingTypeService;
import com.epam.learn.springcore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/training-types")
@RequiredArgsConstructor
@Tag(name = "REST APIs for TrainingType Resource")
public class TrainingTypeController {
    private final TrainingTypeService trainingTypeService;
    private final UserService userService;
    private final AuthenticationFacade authenticationFacade;

    @Operation(summary = "Get Training Types", description = "Is used to fetch the list of available training types from database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of training types fetched"),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<TrainingType>> getAllTrainingTypes(@RequestHeader HttpHeaders headers) {
        Optional<String[]> token = authenticationFacade.extractAndValidateAuthToken(headers);
        if (token.isPresent()) {
            String username = token.get()[0];
            String password = token.get()[1];
            if (userService.authenticate(username, password)) {
                return new ResponseEntity<>(trainingTypeService.getAllTrainingTypes(), HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
