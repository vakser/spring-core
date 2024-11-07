package com.epam.learn.springcore;

import com.epam.learn.springcore.controller.TrainingTypeController;
import com.epam.learn.springcore.entity.TrainingType;
import com.epam.learn.springcore.service.TrainingTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TrainingTypeControllerTest {

    @Mock
    private TrainingTypeService trainingTypeService;

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

        List<TrainingType> mockTrainingTypes = Arrays.asList(new TrainingType(1, "Type1"), new TrainingType(2, "Type2"));
        when(trainingTypeService.getAllTrainingTypes()).thenReturn(mockTrainingTypes);

        // Act
        ResponseEntity<List<TrainingType>> response = trainingTypeController.getAllTrainingTypes();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTrainingTypes, response.getBody());
        verify(trainingTypeService, times(1)).getAllTrainingTypes();
    }

}
