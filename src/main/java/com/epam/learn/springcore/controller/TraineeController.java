package com.epam.learn.springcore.controller;

import com.epam.learn.springcore.dto.*;
import com.epam.learn.springcore.facade.AuthenticationFacade;
import com.epam.learn.springcore.jwt.JwtTokenUtil;
import com.epam.learn.springcore.service.CustomUserDetailsService;
import com.epam.learn.springcore.service.TraineeService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trainees")
@Tag(name = "REST APIs for Trainee Resource")
public class TraineeController {
    private final TraineeService traineeService;
    private final Timer registerTraineeTimer;
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationFacade authenticationFacade;

    public TraineeController(TraineeService traineeService, MeterRegistry meterRegistry,
                             CustomUserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil,
                             AuthenticationFacade authenticationFacade) {
        this.traineeService = traineeService;
        this.registerTraineeTimer = meterRegistry.timer("trainee.register.time", "method", "registerTrainee");
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationFacade = authenticationFacade;
    }

    @Operation(summary = "Register Trainee", description = "Is used to save trainee into database")
    @ApiResponse(responseCode = "201", description = "New trainee profile created")
    @PostMapping()
    public ResponseEntity<ProfileCreatedResponse> registerTrainee(@Valid @RequestBody TraineeRegistrationRequest traineeRegistrationRequest) {
        UserResponse trainee = registerTraineeTimer.record(() -> traineeService.createTrainee(traineeRegistrationRequest));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(trainee.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return new ResponseEntity<>(new ProfileCreatedResponse(trainee.getUsername(), trainee.getPassword(), token), HttpStatus.CREATED);
    }

    @Operation(summary = "Get Trainee Profile", description = "Is used to fetch information about trainee from database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee found"),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access to requested resource forbidden", content = @Content)
    })
    @GetMapping("/{username}")
    public ResponseEntity<GetTraineeProfileResponse> getTraineeProfile(@Valid @PathVariable String username,
                                                                       @RequestHeader HttpHeaders headers) {
        String token = authenticationFacade.extractAuthToken(headers);
        if (!token.isEmpty() && jwtTokenUtil.getUsernameFromToken(token).equals(username)) {
            GetTraineeProfileResponse getTraineeProfileResponse = traineeService.selectTrainee(username);
            return new ResponseEntity<>(getTraineeProfileResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @Operation(summary = "Update Trainee Profile", description = "Is used to update trainee profile in database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee updated"),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access to requested resource forbidden", content = @Content)
    })
    @PutMapping("/{username}")
    public ResponseEntity<TraineeUpdateResponse> updateTraineeProfile(@PathVariable String username,
                                                                      @Valid @RequestBody TraineeUpdateRequest traineeUpdateRequest,
                                                                      @RequestHeader HttpHeaders headers) {
        String token = authenticationFacade.extractAuthToken(headers);
        if (!token.isEmpty() && jwtTokenUtil.getUsernameFromToken(token).equals(username)) {
            TraineeUpdateResponse traineeUpdateResponse = traineeService.updateTrainee(traineeUpdateRequest);
            return new ResponseEntity<>(traineeUpdateResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @Operation(summary = "Delete Trainee Profile", description = "Is used to delete trainee profile from database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee deleted"),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access to requested resource forbidden", content = @Content)
    })
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTraineeProfile(@PathVariable String username, @RequestHeader HttpHeaders headers) {
        String token = authenticationFacade.extractAuthToken(headers);
        if (!token.isEmpty() && jwtTokenUtil.getUsernameFromToken(token).equals(username)) {
            traineeService.deleteTrainee(username);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @Operation(summary = "Change Trainee Activation Status", description = "Is used to change trainee activation status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee activation status changed or remained unchanged if corresponds to request value"),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access to requested resource forbidden", content = @Content)
    })
    @PatchMapping("/{username}")
    public ResponseEntity<Void> changeActivationStatus(@PathVariable String username,
                                                       @Valid @RequestBody ActivationRequest activationRequest,
                                                       @RequestHeader HttpHeaders headers) {
        String token = authenticationFacade.extractAuthToken(headers);
        if (!token.isEmpty() && jwtTokenUtil.getUsernameFromToken(token).equals(username)) {
            if (!traineeService.selectTrainee(username).getIsActive().equals(activationRequest.getIsActive())) {
                traineeService.changeTraineeActivationStatus(activationRequest);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @Operation(summary = "Get Trainers Not Assigned To Trainee", description = "Is used to fetch the list of trainers not assigned to trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetched the list of not assigned trainers"),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access to requested resource forbidden", content = @Content)
    })
    @GetMapping("/{username}/trainers-not-assigned-to-trainee")
    public ResponseEntity<List<TrainerResponse>> getActiveTrainersNotAssignedToTrainee(@PathVariable String username, @RequestHeader HttpHeaders headers) {
        String token = authenticationFacade.extractAuthToken(headers);
        if (!token.isEmpty() && jwtTokenUtil.getUsernameFromToken(token).equals(username)) {
            List<TrainerResponse> notAssignedTrainers = traineeService.findActiveTrainersNotAssignedToTrainee(username);
            return new ResponseEntity<>(notAssignedTrainers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @Operation(summary = "Update Trainee's Trainers", description = "Is used to update the list of trainers assigned to trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The list of trainers assigned to trainee updated"),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access to requested resource forbidden", content = @Content)
    })
    @PutMapping("/{username}/trainers")
    public ResponseEntity<List<TrainerResponse>> updateTraineeTrainers(@PathVariable String username,
                                                                       @Valid @RequestBody UpdateTraineeTrainersRequest request,
                                                                       @RequestHeader HttpHeaders headers) {
        String token = authenticationFacade.extractAuthToken(headers);
        if (!token.isEmpty() && jwtTokenUtil.getUsernameFromToken(token).equals(username)) {
            List<TrainerResponse> updatedTrainers = traineeService.updateTraineeTrainers(request);
            return new ResponseEntity<>(updatedTrainers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @Operation(summary = "Get Trainee Trainings", description = "Is used to fetch the list of trainings for trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The list of trainings fetched"),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access to requested resource forbidden", content = @Content)
    })
    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TraineeTrainingResponse>> getTraineeTrainings(@PathVariable String username,
                                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
                                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
                                                              @RequestParam(required = false) String trainerName,
                                                              @RequestParam(required = false) String trainingType,
                                                                             @RequestHeader HttpHeaders headers) {
        String token = authenticationFacade.extractAuthToken(headers);
        if (!token.isEmpty() && jwtTokenUtil.getUsernameFromToken(token).equals(username)) {
            List<TraineeTrainingResponse> trainings = traineeService.getTraineeTrainings(username, periodFrom, periodTo, trainerName, trainingType);
            return new ResponseEntity<>(trainings, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

}
