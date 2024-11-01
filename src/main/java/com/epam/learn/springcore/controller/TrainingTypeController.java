package com.epam.learn.springcore.controller;

import com.epam.learn.springcore.entity.TrainingType;
import com.epam.learn.springcore.service.TrainingTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/training-types")
@RequiredArgsConstructor
@Tag(name = "REST APIs for TrainingType Resource")
public class TrainingTypeController {
    private final TrainingTypeService trainingTypeService;

    @Operation(summary = "Get Training Types", description = "Is used to fetch the list of available training types from database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of training types fetched"),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<TrainingType>> getAllTrainingTypes() {
        return new ResponseEntity<>(trainingTypeService.getAllTrainingTypes(), HttpStatus.OK);
    }
}
