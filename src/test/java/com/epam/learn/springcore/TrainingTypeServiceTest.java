package com.epam.learn.springcore;

import com.epam.learn.springcore.dao.TrainingTypeRepository;
import com.epam.learn.springcore.entity.TrainingType;
import com.epam.learn.springcore.service.TrainingTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TrainingTypeServiceTest {
    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingTypeService trainingTypeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllTrainingTypes_ReturnsTrainingTypeList() {
        // Given
        List<TrainingType> trainingTypes = new ArrayList<>();
        trainingTypes.add(new TrainingType(1, "Yoga"));
        trainingTypes.add(new TrainingType(2, "Pilates"));

        // Mock repository behavior
        when(trainingTypeRepository.findAll()).thenReturn(trainingTypes);

        // When
        List<TrainingType> result = trainingTypeService.getAllTrainingTypes();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Yoga", result.get(0).getName());
        assertEquals("Pilates", result.get(1).getName());
        verify(trainingTypeRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllTrainingTypes_ReturnsEmptyList() {
        // Given
        List<TrainingType> emptyTrainingTypes = new ArrayList<>();

        // Mock repository behavior
        when(trainingTypeRepository.findAll()).thenReturn(emptyTrainingTypes);

        // When
        List<TrainingType> result = trainingTypeService.getAllTrainingTypes();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(trainingTypeRepository, times(1)).findAll();
    }
}
