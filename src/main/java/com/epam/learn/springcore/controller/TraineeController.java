package com.epam.learn.springcore.controller;

import com.epam.learn.springcore.dto.*;
import com.epam.learn.springcore.facade.AuthenticationFacade;
import com.epam.learn.springcore.service.TraineeService;
import com.epam.learn.springcore.service.UserService;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/trainees")
@RequiredArgsConstructor
@Tag(name = "REST APIs for Trainee Resource")
public class TraineeController {
    private final TraineeService traineeService;
    private final UserService userService;
    private final AuthenticationFacade authenticationFacade;

    @Operation(summary = "Register Trainee", description = "Is used to save trainee into database")
    @ApiResponse(responseCode = "201", description = "Http Status 201 CREATED")
    @PostMapping()
    public ResponseEntity<UserResponse> registerTrainee(@Valid @RequestBody TraineeRegistrationRequest traineeRegistrationRequest) {
        UserResponse trainee = traineeService.createTrainee(traineeRegistrationRequest);
        return new ResponseEntity<>(trainee, HttpStatus.CREATED);
    }

    @Operation(summary = "Get Trainee Profile", description = "Is used to fetch information about trainee from database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee found"),
            @ApiResponse(responseCode = "400", description = "Usernames in path variable and request authentication header not matching",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content)
    })
    @GetMapping("/{username}")
    public ResponseEntity<GetTraineeProfileResponse> getTraineeProfile(@Valid @PathVariable String username, @RequestHeader HttpHeaders headers) {
        Optional<String[]> token = authenticationFacade.extractAndValidateAuthToken(headers);
        if (token.isPresent()) {
            String authUsername = token.get()[0];
            String authPassword = token.get()[1];
            if (!username.equals(authUsername)) {
                return new  ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (userService.authenticate(authUsername, authPassword)) {
                GetTraineeProfileResponse getTraineeProfileResponse = traineeService.selectTrainee(username);
                return new ResponseEntity<>(getTraineeProfileResponse, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Update Trainee Profile", description = "Is used to update trainee profile in database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee updated"),
            @ApiResponse(responseCode = "400", description = "Usernames in path variable, request authentication header and/or request body not matching",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content)
    })
    @PutMapping("/{username}")
    public ResponseEntity<TraineeUpdateResponse> updateTraineeProfile(@PathVariable String username, @Valid @RequestBody
                                                                      TraineeUpdateRequest traineeUpdateRequest,
                                                                      @RequestHeader HttpHeaders headers) {
        if (headers.containsKey(HttpHeaders.AUTHORIZATION)) {
            String token = Objects.requireNonNull(headers.get(HttpHeaders.AUTHORIZATION)).get(0);
            String authUsername = token.split(":")[0];
            String authPassword = token.split(":")[1];
            if (!username.equals(authUsername) || !username.equals(traineeUpdateRequest.getUsername())) {
                return new  ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (userService.authenticate(authUsername, authPassword)) {
                TraineeUpdateResponse traineeUpdateResponse = traineeService.updateTrainee(traineeUpdateRequest);
                return new ResponseEntity<>(traineeUpdateResponse, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Delete Trainee Profile", description = "Is used to delete trainee profile from database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee deleted"),
            @ApiResponse(responseCode = "400", description = "Usernames in path variable and request authentication header not matching",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content)
    })
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTraineeProfile(@PathVariable String username, @RequestHeader HttpHeaders headers) {
        Optional<String[]> token = authenticationFacade.extractAndValidateAuthToken(headers);
        if (token.isPresent()) {
            String authUsername = token.get()[0];
            String authPassword = token.get()[1];
            if (!username.equals(authUsername)) {
                return new  ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (userService.authenticate(authUsername, authPassword)) {
                traineeService.deleteTrainee(username);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Change Trainee Activation Status", description = "Is used to change trainee activation status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee activation status changed or remained unchanged if corresponds to request value"),
            @ApiResponse(responseCode = "400", description = "Usernames in path variable, request authentication header and/or request body not matching",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content)
    })
    @PatchMapping("/{username}")
    public ResponseEntity<Void> changeActivationStatus(@PathVariable String username,
                                                       @Valid @RequestBody ActivationRequest activationRequest,
                                                       @RequestHeader HttpHeaders headers) {
        Optional<String[]> token = authenticationFacade.extractAndValidateAuthToken(headers);
        if (token.isPresent()) {
            String authUsername = token.get()[0];
            String authPassword = token.get()[1];
            if (!username.equals(authUsername) || !username.equals(activationRequest.getUsername())) {
                return new  ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (userService.authenticate(authUsername, authPassword)) {
                if (!traineeService.selectTrainee(username).getIsActive().equals(activationRequest.getIsActive())) {
                    traineeService.changeTraineeActivationStatus(activationRequest);
                }
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Get Trainers Not Assigned To Trainee", description = "Is used to fetch the list of trainers not assigned to trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetched the list of not assigned trainers"),
            @ApiResponse(responseCode = "400", description = "Usernames in path variable and request authentication header not matching",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content)
    })
    @GetMapping("/{username}/trainers-not-assigned-to-trainee")
    public ResponseEntity<List<TrainerResponse>> getNotAssignedToTrainee(@PathVariable String username, @RequestHeader HttpHeaders headers) {
        Optional<String[]> token = authenticationFacade.extractAndValidateAuthToken(headers);
        if (token.isPresent()) {
            String authUsername = token.get()[0];
            String authPassword = token.get()[1];
            if (!username.equals(authUsername)) {
                return new  ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (userService.authenticate(authUsername, authPassword)) {
                traineeService.selectTrainee(username);
                List<TrainerResponse> notAssignedTrainers = traineeService.findActiveTrainersNotAssignedToTrainee(username);
                return new ResponseEntity<>(notAssignedTrainers, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Update Trainee's Trainers", description = "Is used to update the list of trainers assigned to trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The list of trainers assigned to trainee updated"),
            @ApiResponse(responseCode = "400", description = "Usernames in path variable, request authentication header and/or request body not matching",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content)
    })
    @PutMapping("/{username}/trainers")
    public ResponseEntity<List<TrainerResponse>> updateTraineeTrainers(@PathVariable String username,
                                                                       @Valid @RequestBody UpdateTraineeTrainersRequest request,
                                                                       @RequestHeader HttpHeaders headers) {
        Optional<String[]> token = authenticationFacade.extractAndValidateAuthToken(headers);
        if (token.isPresent()) {
            String authUsername = token.get()[0];
            String authPassword = token.get()[1];
            if (!username.equals(authUsername) || !username.equals(request.getTraineeUsername())) {
                return new  ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (userService.authenticate(authUsername, authPassword)) {
                List<TrainerResponse> updatedTrainers = traineeService.updateTraineeTrainers(request);
                return new ResponseEntity<>(updatedTrainers, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Get Trainee Trainings", description = "Is used to fetch the list of trainings for trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The list of trainings fetched"),
            @ApiResponse(responseCode = "400", description = "Usernames in path variable and request authentication header not matching",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content)
    })
    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TraineeTrainingResponse>> getTraineeTrainings(@PathVariable String username,
                                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
                                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
                                                              @RequestParam(required = false) String trainerName,
                                                              @RequestParam(required = false) String trainingType,
                                                              @RequestHeader HttpHeaders headers) {
        Optional<String[]> token = authenticationFacade.extractAndValidateAuthToken(headers);
        if (token.isPresent()) {
            String authUsername = token.get()[0];
            String authPassword = token.get()[1];
            if (!username.equals(authUsername)) {
                return new  ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (userService.authenticate(authUsername, authPassword)) {
                List<TraineeTrainingResponse> trainings = traineeService.getTraineeTrainings(username, periodFrom, periodTo, trainerName,  trainingType);
                return new ResponseEntity<>(trainings, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
