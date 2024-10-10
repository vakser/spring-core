package com.epam.learn.springcore.controller;

import com.epam.learn.springcore.entity.TrainingType;
import com.epam.learn.springcore.service.TrainingTypeService;
import com.epam.learn.springcore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/training-types")
@RequiredArgsConstructor
@Tag(name = "REST APIs for TrainingType Resource")
public class TrainingTypeController {
    private final TrainingTypeService trainingTypeService;
    private final UserService userService;

    @Operation(summary = "Get Training Types", description = "Is used to fetch the list of available training types from database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of training types fetched"),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content)
    })
    @GetMapping
    public List<TrainingType> getAllTrainingTypes(@RequestHeader HttpHeaders headers) {
        if (headers.containsKey(HttpHeaders.AUTHORIZATION)) {
            String token = Objects.requireNonNull(headers.get(HttpHeaders.AUTHORIZATION)).get(0);
            String username = token.split(":")[0];
            String password = token.split(":")[1];
            if (userService.authenticate(username, password)) {
                return trainingTypeService.getAllTrainingTypes();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
