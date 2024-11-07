package com.epam.learn.springcore;

import com.epam.learn.springcore.controller.TraineeController;
import com.epam.learn.springcore.dto.*;
import com.epam.learn.springcore.facade.AuthenticationFacade;
import com.epam.learn.springcore.jwt.JwtTokenUtil;
import com.epam.learn.springcore.service.CustomUserDetailsService;
import com.epam.learn.springcore.service.TraineeService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TraineeControllerTest {
    @Mock
    private TraineeService traineeService;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @InjectMocks
    private TraineeController traineeController;

    private Timer registerTraineeTimer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registerTraineeTimer = mock(Timer.class);
        when(meterRegistry.timer("trainee.register.time", "method", "registerTrainee")).thenReturn(registerTraineeTimer);
    }

    @Test
    void testRegisterTrainee_Success() {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest("John", "Doe", LocalDate.of(1990, 1, 1), "Somewhere");
        UserResponse userResponse = new UserResponse("John.Doe", "hashed_password");
        UserDetails userDetails = mock(UserDetails.class);
        ProfileCreatedResponse expectedResponse = new ProfileCreatedResponse("John.Doe", "hashed_password", "jwt_token");

        when(registerTraineeTimer.record(() -> traineeService.createTrainee(request))).thenReturn(userResponse);
        when(userDetailsService.loadUserByUsername("John.Doe")).thenReturn(userDetails);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn("jwt_token");

        ResponseEntity<ProfileCreatedResponse> response = traineeController.registerTrainee(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void testGetTraineeProfile_Success() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer jwt_token");
        GetTraineeProfileResponse profileResponse = new GetTraineeProfileResponse("John", "Doe", LocalDate.of(1990, 1, 1), "somewhere", true, new ArrayList<>());

        when(authenticationFacade.extractAuthToken(headers)).thenReturn("jwt_token");
        when(jwtTokenUtil.getUsernameFromToken("jwt_token")).thenReturn("username");
        when(traineeService.selectTrainee("username")).thenReturn(profileResponse);

        ResponseEntity<GetTraineeProfileResponse> response = traineeController.getTraineeProfile("username", headers);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(profileResponse, response.getBody());
    }

    @Test
    void testGetTraineeProfile_Forbidden() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer jwt_token");

        when(authenticationFacade.extractAuthToken(headers)).thenReturn("jwt_token");
        when(jwtTokenUtil.getUsernameFromToken("jwt_token")).thenReturn("another_user");

        ResponseEntity<GetTraineeProfileResponse> response = traineeController.getTraineeProfile("username", headers);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testUpdateTraineeProfile_Success() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer jwt_token");
        TraineeUpdateRequest updateRequest = new TraineeUpdateRequest("John.Doe", "John", "Doe", LocalDate.of(1990, 1, 1), "somewhere", true);
        TraineeUpdateResponse updateResponse = new TraineeUpdateResponse("John.Doe","John", "Doe", LocalDate.of(1990, 1, 1), "somewhere", true, new ArrayList<>());

        when(authenticationFacade.extractAuthToken(headers)).thenReturn("jwt_token");
        when(jwtTokenUtil.getUsernameFromToken("jwt_token")).thenReturn("username");
        when(traineeService.updateTrainee(updateRequest)).thenReturn(updateResponse);

        ResponseEntity<TraineeUpdateResponse> response = traineeController.updateTraineeProfile("username", updateRequest, headers);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updateResponse, response.getBody());
    }

    @Test
    void testDeleteTraineeProfile_Success() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer jwt_token");

        when(authenticationFacade.extractAuthToken(headers)).thenReturn("jwt_token");
        when(jwtTokenUtil.getUsernameFromToken("jwt_token")).thenReturn("username");

        ResponseEntity<Void> response = traineeController.deleteTraineeProfile("username", headers);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(traineeService, times(1)).deleteTrainee("username");
    }

    @Test
    void testGetActiveTrainersNotAssignedToTrainee_Success() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer jwt_token");
        List<TrainerResponse> trainers = Collections.singletonList(new TrainerResponse("John.Doe", "John", "Doe", 1));

        when(authenticationFacade.extractAuthToken(headers)).thenReturn("jwt_token");
        when(jwtTokenUtil.getUsernameFromToken("jwt_token")).thenReturn("username");
        when(traineeService.findActiveTrainersNotAssignedToTrainee("username")).thenReturn(trainers);

        ResponseEntity<List<TrainerResponse>> response = traineeController.getActiveTrainersNotAssignedToTrainee("username", headers);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(trainers, response.getBody());
    }

    @Test
    void testGetTraineeTrainings_Success() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer jwt_token");
        List<TraineeTrainingResponse> trainings = Collections.singletonList(new TraineeTrainingResponse("training", LocalDate.of(2024, 11, 11), "Yoga", 60, "John.Doe"));

        when(authenticationFacade.extractAuthToken(headers)).thenReturn("jwt_token");
        when(jwtTokenUtil.getUsernameFromToken("jwt_token")).thenReturn("username");
        when(traineeService.getTraineeTrainings("username", null, null, null, null)).thenReturn(trainings);

        ResponseEntity<List<TraineeTrainingResponse>> response = traineeController.getTraineeTrainings("username", null, null, null, null, headers);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(trainings, response.getBody());
    }
}
