package com.epam.learn.springcore.controller;

import com.epam.learn.springcore.dto.*;
import com.epam.learn.springcore.facade.AuthenticationFacade;
import com.epam.learn.springcore.jwt.JwtTokenUtil;
import com.epam.learn.springcore.service.CustomUserDetailsService;
import com.epam.learn.springcore.service.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
@Tag(name = "REST APIs for Trainer Resource")
public class TrainerController {
    private final TrainerService trainerService;
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationFacade authenticationFacade;

    @Operation(summary = "Register Trainer", description = "Is used to save trainer into database")
    @ApiResponse(responseCode = "201", description = "Http Status 201 CREATED")
    @PostMapping()
    public ResponseEntity<ProfileCreatedResponse> registerTrainer(@Valid @RequestBody TrainerRegistrationRequest trainerRegistrationRequest) {
        UserResponse trainer = trainerService.createTrainer(trainerRegistrationRequest);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(trainer.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return new ResponseEntity<>(new ProfileCreatedResponse(trainer.getUsername(), trainer.getPassword(), token), HttpStatus.CREATED);
    }

    @Operation(summary = "Get Trainer Profile", description = "Is used to fetch information about trainer from database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer found"),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access to requested resource forbidden", content = @Content)
    })
    @GetMapping("/{username}")
    public ResponseEntity<GetTrainerProfileResponse> getTrainerProfile(@PathVariable String username, @RequestHeader HttpHeaders headers) {
        String token = authenticationFacade.extractAuthToken(headers);
        if (!token.isEmpty() && jwtTokenUtil.getUsernameFromToken(token).equals(username)) {
            GetTrainerProfileResponse getTrainerProfileResponse = trainerService.selectTrainer(username);
            return new ResponseEntity<>(getTrainerProfileResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @Operation(summary = "Update Trainer Profile", description = "Is used to update trainer profile in database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer updated"),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access to requested resource forbidden", content = @Content)
    })
    @PutMapping("/{username}")
    public ResponseEntity<TrainerUpdateResponse> updateTrainerProfile(@PathVariable String username,
                                                                      @Valid @RequestBody TrainerUpdateRequest trainerUpdateRequest,
                                                                      @RequestHeader HttpHeaders headers) {
        String token = authenticationFacade.extractAuthToken(headers);
        if (!token.isEmpty() && jwtTokenUtil.getUsernameFromToken(token).equals(username)) {
            TrainerUpdateResponse trainerUpdateResponse = trainerService.updateTrainer(trainerUpdateRequest);
            return new ResponseEntity<>(trainerUpdateResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @Operation(summary = "Change Trainer Activation Status", description = "Is used to change trainer activation status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer activation status changed or remained unchanged if corresponds to request value"),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access to requested resource forbidden", content = @Content)
    })
    @PatchMapping("/{username}")
    public ResponseEntity<Void> changeActivationStatus(@PathVariable String username,
                                                       @Valid @RequestBody ActivationRequest activationRequest,
                                                       @RequestHeader HttpHeaders headers) {
        String token = authenticationFacade.extractAuthToken(headers);
        if (!token.isEmpty() && jwtTokenUtil.getUsernameFromToken(token).equals(username)) {
            if (!trainerService.selectTrainer(username).getIsActive().equals(activationRequest.getIsActive())) {
                trainerService.changeTrainerActivationStatus(activationRequest);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @Operation(summary = "Get Trainer Trainings", description = "Is used to fetch the list of trainings for trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The list of trainings fetched"),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access to requested resource forbidden", content = @Content)
    })
    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainerTrainingResponse>> getTrainerTrainings(@PathVariable String username,
                                                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
                                                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
                                                                             @RequestParam(required = false) String traineeName, @RequestHeader HttpHeaders headers) {

        String token = authenticationFacade.extractAuthToken(headers);
        if (!token.isEmpty() && jwtTokenUtil.getUsernameFromToken(token).equals(username)) {
            List<TrainerTrainingResponse> trainings = trainerService.getTrainerTrainings(username, periodFrom, periodTo, traineeName);
            return new ResponseEntity<>(trainings, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @Operation(summary = "Add Training", description = "Is used to add a training for trainee by trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The training added"),
            @ApiResponse(responseCode = "404", description = "Trainer or trainee not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access to requested resource forbidden", content = @Content)
    })
    @PostMapping("/{username}/trainings")
    public ResponseEntity<Void> addTraining(@PathVariable String username,
                                            @Valid @RequestBody AddTrainingRequest addTrainingRequest,
                                            @RequestHeader HttpHeaders headers) {
        String token = authenticationFacade.extractAuthToken(headers);
        if (!token.isEmpty() && jwtTokenUtil.getUsernameFromToken(token).equals(username)) {
            trainerService.addTraining(addTrainingRequest);
            return ResponseEntity.ok().build();
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

}
