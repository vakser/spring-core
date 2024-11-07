package com.epam.learn.springcore;

import com.epam.learn.springcore.controller.TrainerController;
import com.epam.learn.springcore.dto.*;
import com.epam.learn.springcore.facade.AuthenticationFacade;
import com.epam.learn.springcore.jwt.JwtTokenUtil;
import com.epam.learn.springcore.service.CustomUserDetailsService;
import com.epam.learn.springcore.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TrainerControllerTest {
    @Mock
    private TrainerService trainerService;
    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private CustomUserDetailsService userDetailsService;
    @Mock
    private JwtTokenUtil jwtTokenUtil;
    @InjectMocks
    private TrainerController trainerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Unit test for getting a trainer profile with successful authentication
    @Test
    void testGetTrainerProfile_Success() {
        // Arrange
        String username = "trainer1";
        HttpHeaders headers = new HttpHeaders();
        String token = "token";
        GetTrainerProfileResponse expectedResponse = new GetTrainerProfileResponse();

        when(authenticationFacade.extractAuthToken(headers)).thenReturn(token);
        when(trainerService.selectTrainer(username)).thenReturn(expectedResponse);
        when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn(username);

        // Act
        ResponseEntity<GetTrainerProfileResponse> response = trainerController.getTrainerProfile(username, headers);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(trainerService, times(1)).selectTrainer(username);
    }

    @Test
    public void testGetTrainerProfile_Forbidden() {
        String username = "trainer123";
        HttpHeaders headers = new HttpHeaders();

        when(authenticationFacade.extractAuthToken(headers)).thenReturn("invalidToken");
        when(jwtTokenUtil.getUsernameFromToken("invalidToken")).thenReturn("anotherUser");

        ResponseEntity<GetTrainerProfileResponse> response = trainerController.getTrainerProfile(username, headers);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(trainerService, never()).selectTrainer(username);
    }

    @Test
    public void testRegisterTrainer() {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        UserResponse userResponse = new UserResponse("trainer123", "password123");
        UserDetails userDetails = mock(UserDetails.class);

        when(trainerService.createTrainer(request)).thenReturn(userResponse);
        when(userDetailsService.loadUserByUsername("trainer123")).thenReturn(userDetails);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn("mockToken");

        ResponseEntity<ProfileCreatedResponse> response = trainerController.registerTrainer(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("trainer123", Objects.requireNonNull(response.getBody()).getUsername());
        verify(trainerService).createTrainer(request);
    }

    // Unit test for updating a trainer profile with successful authentication
    @Test
    void testUpdateTrainerProfile_Success() {
        // Arrange
        String username = "trainer1";
        HttpHeaders headers = new HttpHeaders();
        String token = "token";
        TrainerUpdateRequest updateRequest = new TrainerUpdateRequest();
        updateRequest.setUsername(username);

        TrainerUpdateResponse expectedResponse = new TrainerUpdateResponse();
        when(authenticationFacade.extractAuthToken(headers)).thenReturn(token);
        when(trainerService.updateTrainer(updateRequest)).thenReturn(expectedResponse);
        when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn(username);

        // Act
        ResponseEntity<TrainerUpdateResponse> response = trainerController.updateTrainerProfile(username, updateRequest, headers);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(trainerService, times(1)).updateTrainer(updateRequest);
    }

    // Unit test for changing trainer activation status with successful authentication
    @Test
    void testChangeActivationStatus_Success() {
        // Arrange
        String username = "trainer1";
        HttpHeaders headers = new HttpHeaders();
        String token = "token";
        ActivationRequest activationRequest = new ActivationRequest();
        activationRequest.setUsername(username);
        activationRequest.setIsActive(true);

        GetTrainerProfileResponse trainerProfile = new GetTrainerProfileResponse();
        trainerProfile.setIsActive(false);  // Trainer is not active initially
        when(authenticationFacade.extractAuthToken(headers)).thenReturn(token);
        when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn(username);
        when(trainerService.selectTrainer(username)).thenReturn(trainerProfile);

        // Act
        ResponseEntity<Void> response = trainerController.changeActivationStatus(username, activationRequest, headers);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(trainerService, times(1)).changeTrainerActivationStatus(activationRequest);
    }

    // Unit test for fetching trainer trainings with successful authentication
    @Test
    void testGetTrainerTrainings_Success() {
        // Arrange
        String username = "trainer1";
        HttpHeaders headers = new HttpHeaders();
        String token = "token";

        List<TrainerTrainingResponse> expectedTrainings = List.of(new TrainerTrainingResponse());
        when(authenticationFacade.extractAuthToken(headers)).thenReturn(token);
        when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn(username);
        when(trainerService.getTrainerTrainings(username, null, null, null)).thenReturn(expectedTrainings);

        // Act
        ResponseEntity<List<TrainerTrainingResponse>> response = trainerController.getTrainerTrainings(username, null,
                null, null, headers);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedTrainings, response.getBody());
        verify(trainerService, times(1)).getTrainerTrainings(username, null, null, null);
    }

    // Unit test for adding a training for a trainer with successful authentication
    @Test
    void testAddTraining_Success() {
        // Arrange
        String username = "trainer1";
        HttpHeaders headers = new HttpHeaders();
        String token = "token";
        AddTrainingRequest addTrainingRequest = new AddTrainingRequest();
        addTrainingRequest.setTrainerUsername(username);

        when(authenticationFacade.extractAuthToken(headers)).thenReturn(token);
        when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn(username);

        // Act
        ResponseEntity<Void> response = trainerController.addTraining(username, addTrainingRequest, headers);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(trainerService, times(1)).addTraining(addTrainingRequest);
    }

}