package com.epam.learn.springcore;

import com.epam.learn.springcore.controller.TrainerController;
import com.epam.learn.springcore.dto.*;
import com.epam.learn.springcore.facade.AuthenticationFacade;
import com.epam.learn.springcore.service.TrainerService;
import com.epam.learn.springcore.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TrainerControllerTest {
    @Mock
    private TrainerService trainerService;
    @Mock
    private UserService userService;
    @Mock
    private AuthenticationFacade authenticationFacade;
    @InjectMocks
    private TrainerController trainerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Unit test for registering a trainer
    @Test
    void testRegisterTrainer_Success() {
        // Arrange
        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        UserResponse expectedResponse = new UserResponse();
        when(trainerService.createTrainer(request)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<UserResponse> response = trainerController.registerTrainer(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(trainerService, times(1)).createTrainer(request);
    }

    // Unit test for getting a trainer profile with successful authentication
    @Test
    void testGetTrainerProfile_Success() {
        // Arrange
        String username = "trainer1";
        HttpHeaders headers = new HttpHeaders();
        String[] validToken = {username, "password"};
        when(authenticationFacade.extractAndValidateAuthToken(headers)).thenReturn(Optional.of(validToken));
        when(userService.authenticate(username, "password")).thenReturn(true);

        GetTrainerProfileResponse expectedResponse = new GetTrainerProfileResponse();
        when(trainerService.selectTrainer(username)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<GetTrainerProfileResponse> response = trainerController.getTrainerProfile(username, headers);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(trainerService, times(1)).selectTrainer(username);
    }

    // Unit test for getting a trainer profile with username mismatch
    @Test
    void testGetTrainerProfile_UsernameMismatch() {
        // Arrange
        String username = "trainer1";
        HttpHeaders headers = new HttpHeaders();
        String[] validToken = {"differentUser", "password"};
        when(authenticationFacade.extractAndValidateAuthToken(headers)).thenReturn(Optional.of(validToken));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> trainerController.getTrainerProfile(username, headers));
        verify(trainerService, never()).selectTrainer(anyString());
    }

    // Unit test for updating a trainer profile with successful authentication
    @Test
    void testUpdateTrainerProfile_Success() {
        // Arrange
        String username = "trainer1";
        TrainerUpdateRequest updateRequest = new TrainerUpdateRequest();
        updateRequest.setUsername(username);
        HttpHeaders headers = new HttpHeaders();
        String[] validToken = {username, "password"};
        when(authenticationFacade.extractAndValidateAuthToken(headers)).thenReturn(Optional.of(validToken));
        when(userService.authenticate(username, "password")).thenReturn(true);

        TrainerUpdateResponse expectedResponse = new TrainerUpdateResponse();
        when(trainerService.updateTrainer(updateRequest)).thenReturn(expectedResponse);

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
        ActivationRequest activationRequest = new ActivationRequest();
        activationRequest.setUsername(username);
        activationRequest.setIsActive(true);
        HttpHeaders headers = new HttpHeaders();
        String[] validToken = {username, "password"};
        when(authenticationFacade.extractAndValidateAuthToken(headers)).thenReturn(Optional.of(validToken));
        when(userService.authenticate(username, "password")).thenReturn(true);

        GetTrainerProfileResponse trainerProfile = new GetTrainerProfileResponse();
        trainerProfile.setIsActive(false);  // Trainer is not active initially
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
        String[] validToken = {username, "password"};
        when(authenticationFacade.extractAndValidateAuthToken(headers)).thenReturn(Optional.of(validToken));
        when(userService.authenticate(username, "password")).thenReturn(true);

        List<TrainerTrainingResponse> expectedTrainings = List.of(new TrainerTrainingResponse());
        when(trainerService.getTrainerTrainings(username, null, null, null)).thenReturn(expectedTrainings);

        // Act
        ResponseEntity<List<TrainerTrainingResponse>> response = trainerController.getTrainerTrainings(username, null, null, null, headers);

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
        AddTrainingRequest addTrainingRequest = new AddTrainingRequest();
        addTrainingRequest.setTrainerUsername(username);
        HttpHeaders headers = new HttpHeaders();
        String[] validToken = {username, "password"};
        when(authenticationFacade.extractAndValidateAuthToken(headers)).thenReturn(Optional.of(validToken));
        when(userService.authenticate(username, "password")).thenReturn(true);

        // Act
        ResponseEntity<Void> response = trainerController.addTraining(username, addTrainingRequest, headers);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(trainerService, times(1)).addTraining(addTrainingRequest);
    }

}
