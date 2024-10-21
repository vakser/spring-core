package com.epam.learn.springcore;

import com.epam.learn.springcore.controller.TrainingTypeController;
import com.epam.learn.springcore.entity.TrainingType;
import com.epam.learn.springcore.facade.AuthenticationFacade;
import com.epam.learn.springcore.service.TrainingTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.epam.learn.springcore.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TrainingTypeControllerTest {

    @Mock
    private TrainingTypeService trainingTypeService;
    @Mock
    private UserService userService;
    @Mock
    private AuthenticationFacade authenticationFacade;
    @InjectMocks
    private TrainingTypeController trainingTypeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Unit test for successful fetch of training types with valid authentication
    @Test
    void testGetAllTrainingTypes_Successful() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        String[] validToken = {"validUser", "validPassword"};
        when(authenticationFacade.extractAndValidateAuthToken(headers)).thenReturn(Optional.of(validToken));
        when(userService.authenticate("validUser", "validPassword")).thenReturn(true);

        List<TrainingType> mockTrainingTypes = Arrays.asList(new TrainingType(1, "Type1"), new TrainingType(2, "Type2"));
        when(trainingTypeService.getAllTrainingTypes()).thenReturn(mockTrainingTypes);

        // Act
        ResponseEntity<List<TrainingType>> response = trainingTypeController.getAllTrainingTypes(headers);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTrainingTypes, response.getBody());
        verify(authenticationFacade, times(1)).extractAndValidateAuthToken(headers);
        verify(userService, times(1)).authenticate("validUser", "validPassword");
        verify(trainingTypeService, times(1)).getAllTrainingTypes();
    }

    // Unit test for failed authentication
    @Test
    void testGetAllTrainingTypes_Unauthorized_AuthenticationFailure() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        String[] validToken = {"validUser", "invalidPassword"};
        when(authenticationFacade.extractAndValidateAuthToken(headers)).thenReturn(Optional.of(validToken));
        when(userService.authenticate("validUser", "invalidPassword")).thenReturn(false);

        // Act
        ResponseEntity<List<TrainingType>> response = trainingTypeController.getAllTrainingTypes(headers);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
        verify(authenticationFacade, times(1)).extractAndValidateAuthToken(headers);
        verify(userService, times(1)).authenticate("validUser", "invalidPassword");
        verify(trainingTypeService, never()).getAllTrainingTypes();
    }

    // Unit test for invalid token (missing or incorrect token)
    @Test
    void testGetAllTrainingTypes_Unauthorized_InvalidToken() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        when(authenticationFacade.extractAndValidateAuthToken(headers)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<List<TrainingType>> response = trainingTypeController.getAllTrainingTypes(headers);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
        verify(authenticationFacade, times(1)).extractAndValidateAuthToken(headers);
        verify(userService, never()).authenticate(anyString(), anyString());
        verify(trainingTypeService, never()).getAllTrainingTypes();
    }
}
