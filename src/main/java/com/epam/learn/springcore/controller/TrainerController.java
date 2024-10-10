package com.epam.learn.springcore.controller;

import com.epam.learn.springcore.dto.*;
import com.epam.learn.springcore.service.TrainerService;
import com.epam.learn.springcore.service.UserService;
import com.epam.learn.springcore.specification.TrainerTrainingSearchCriteria;
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
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
@Tag(name = "REST APIs for Trainer Resource")
public class TrainerController {
    private final TrainerService trainerService;
    private final UserService userService;

    @Operation(summary = "Register Trainer", description = "Is used to save trainer into database")
    @ApiResponse(responseCode = "201", description = "Http Status 201 CREATED")
    @PostMapping()
    public ResponseEntity<UserResponse> registerTrainer(@Valid @RequestBody TrainerRegistrationRequest trainerRegistrationRequest) {
        UserResponse trainer = trainerService.createTrainer(trainerRegistrationRequest);
        return new ResponseEntity<>(trainer, HttpStatus.CREATED);
    }

    @Operation(summary = "Get Trainer Profile", description = "Is used to fetch information about trainer from database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer found"),
            @ApiResponse(responseCode = "400", description = "Usernames in path variable and request authentication header not matching",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content)
    })
    @GetMapping("/{username}")
    public ResponseEntity<GetTrainerProfileResponse> getTrainerProfile(@PathVariable String username, @RequestHeader HttpHeaders headers) {
        if (headers.containsKey(HttpHeaders.AUTHORIZATION)) {
            String token = Objects.requireNonNull(headers.get(HttpHeaders.AUTHORIZATION)).get(0);
            String authUsername = token.split(":")[0];
            String authPassword = token.split(":")[1];
            if (!username.equals(authUsername)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username mismatch");
            }
            if (userService.authenticate(authUsername, authPassword)) {
                GetTrainerProfileResponse getTrainerProfileResponse = trainerService.selectTrainer(username);
                return new ResponseEntity<>(getTrainerProfileResponse, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Update Trainer Profile", description = "Is used to update trainer profile in database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer updated"),
            @ApiResponse(responseCode = "400", description = "Usernames in path variable, request authentication header and/or request body not matching",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content)
    })
    @PutMapping("/{username}")
    public ResponseEntity<TrainerUpdateResponse> updateTrainerProfile(@PathVariable String username,
                                                                      @Valid @RequestBody TrainerUpdateRequest trainerUpdateRequest,
                                                                      @RequestHeader HttpHeaders headers) {
        if (headers.containsKey(HttpHeaders.AUTHORIZATION)) {
            String token = Objects.requireNonNull(headers.get(HttpHeaders.AUTHORIZATION)).get(0);
            String authUsername = token.split(":")[0];
            String authPassword = token.split(":")[1];
            if (!username.equals(authUsername) || !username.equals(trainerUpdateRequest.getUsername())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username mismatch");
            }
            if (userService.authenticate(authUsername, authPassword)) {
                TrainerUpdateResponse trainerUpdateResponse = trainerService.updateTrainer(trainerUpdateRequest);
                return new ResponseEntity<>(trainerUpdateResponse, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Change Trainer Activation Status", description = "Is used to change trainer activation status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer activation status changed or remained unchanged if corresponds to request value"),
            @ApiResponse(responseCode = "400", description = "Usernames in path variable, request authentication header and/or request body not matching",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content)
    })
    @PatchMapping("/{username}")
    public ResponseEntity<Void> changeActivationStatus(@PathVariable String username,
                                                       @Valid @RequestBody ActivationRequest activationRequest,
                                                       @RequestHeader HttpHeaders headers) {
        if (headers.containsKey(HttpHeaders.AUTHORIZATION)) {
            String token = Objects.requireNonNull(headers.get(HttpHeaders.AUTHORIZATION)).get(0);
            String authUsername = token.split(":")[0];
            String authPassword = token.split(":")[1];
            if (!username.equals(authUsername) || !username.equals(activationRequest.getUsername())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username mismatch");
            }
            if (userService.authenticate(authUsername, authPassword)) {
                if (!trainerService.selectTrainer(username).getIsActive().equals(activationRequest.getIsActive())) {
                    trainerService.changeTrainerActivationStatus(activationRequest);
                }
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Get Trainer Trainings", description = "Is used to fetch the list of trainings for trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The list of trainings fetched"),
            @ApiResponse(responseCode = "400", description = "Usernames in path variable and request authentication header not matching",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content)
    })
    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainerTrainingResponse>> getTrainerTrainings(@PathVariable String username,
                                                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
                                                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
                                                                             @RequestParam(required = false) String traineeName,
                                                                             @RequestHeader HttpHeaders headers) {
        if (headers.containsKey(HttpHeaders.AUTHORIZATION)) {
            String token = Objects.requireNonNull(headers.get(HttpHeaders.AUTHORIZATION)).get(0);
            String authUsername = token.split(":")[0];
            String authPassword = token.split(":")[1];
            if (!username.equals(authUsername)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username mismatch");
            }
            if (userService.authenticate(authUsername, authPassword)) {
                TrainerTrainingSearchCriteria criteria = new TrainerTrainingSearchCriteria(username, periodFrom, periodTo, traineeName);
                List<TrainerTrainingResponse> trainings = trainerService.getTrainerTrainings(criteria);
                return new ResponseEntity<>(trainings, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Add Training", description = "Is used to add a training for trainee by trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The training added"),
            @ApiResponse(responseCode = "400", description = "Usernames in path variable, request authentication header and/or request body not matching",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainer or trainee not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Request lacks valid authentication credentials", content = @Content)
    })
    @PostMapping("/{username}/trainings")
    public ResponseEntity<Void> addTraining(@PathVariable String username,
                                            @Valid @RequestBody AddTrainingRequest addTrainingRequest,
                                            @RequestHeader HttpHeaders headers) {
        if (headers.containsKey(HttpHeaders.AUTHORIZATION)) {
            String token = Objects.requireNonNull(headers.get(HttpHeaders.AUTHORIZATION)).get(0);
            String authUsername = token.split(":")[0];
            String authPassword = token.split(":")[1];
            if (!username.equals(authUsername) || !username.equals(addTrainingRequest.getTrainerUsername())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username mismatch");
            }
            if (userService.authenticate(authUsername, authPassword)) {
                trainerService.addTraining(addTrainingRequest);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
