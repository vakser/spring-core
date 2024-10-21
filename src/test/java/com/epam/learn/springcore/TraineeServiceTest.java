package com.epam.learn.springcore;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.epam.learn.springcore.dao.TraineeRepository;
import com.epam.learn.springcore.dao.TrainingRepository;
import com.epam.learn.springcore.dto.*;
import com.epam.learn.springcore.entity.*;
import com.epam.learn.springcore.service.TraineeService;
import com.epam.learn.springcore.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import java.util.Collections;

public class TraineeServiceTest {
    @InjectMocks
    private TraineeService traineeService;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private UserService userService;

    @Mock
    private TrainingRepository trainingRepository;


    private Trainee trainee;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Sample data setup
        User user = new User();
        user.setUsername("testuser");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user.setIsActive(false);

        trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
        trainee.setAddress("123 Test St");

        trainer = new Trainer();
        trainer.setUser(user);
        TrainingType trainingType = new TrainingType();
        trainingType.setId(1);
        trainer.setSpecialization(trainingType);
    }

    @Test
    @Transactional
    void testCreateTrainee() {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setFirstName("Test");
        request.setLastName("User");
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));
        request.setAddress("123 Test St");

        when(userService.calculateUsername("Test", "User")).thenReturn("testuser");
        when(userService.generateRandomPassword()).thenReturn("password");
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);

        UserResponse response = traineeService.createTrainee(request);

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals("password", response.getPassword());
        verify(traineeRepository, times(1)).save(any(Trainee.class));
    }

    @Test
    void testUpdateTrainee() {
        TraineeUpdateRequest request = new TraineeUpdateRequest();
        request.setUsername("testuser");
        request.setFirstName("UpdatedFirstName");
        request.setLastName("UpdatedLastName");
        request.setDateOfBirth(LocalDate.of(1999, 12, 31));
        request.setAddress("Updated Address");

        when(traineeRepository.findByUsername("testuser")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);

        TraineeUpdateResponse response = traineeService.updateTrainee(request);

        assertNotNull(response);
        assertEquals("UpdatedFirstName", response.getFirstName());
        assertEquals("UpdatedLastName", response.getLastName());
        assertEquals("Updated Address", trainee.getAddress());
        verify(traineeRepository, times(1)).save(trainee);
    }

    @Test
    void testDeleteTrainee() {
        when(traineeRepository.findByUsername("testuser")).thenReturn(Optional.of(trainee));

        traineeService.deleteTrainee("testuser");

        verify(traineeRepository, times(1)).delete(trainee);
    }

    @Test
    void testSelectTrainee() {
        trainee.setTrainers(new ArrayList<>());
        when(traineeRepository.findByUsername("testuser")).thenReturn(Optional.of(trainee));

        GetTraineeProfileResponse response = traineeService.selectTrainee("testuser");

        assertNotNull(response);
        assertEquals("Test", response.getFirstName());
        assertEquals("User", response.getLastName());
        verify(traineeRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testChangeTraineeActivationStatus() {
        ActivationRequest activationRequest = new ActivationRequest();
        activationRequest.setUsername("testuser");
        activationRequest.setIsActive(true);

        when(traineeRepository.findByUsername("testuser")).thenReturn(Optional.of(trainee));

        traineeService.changeTraineeActivationStatus(activationRequest);

        assertTrue(trainee.getUser().getIsActive());
        verify(traineeRepository, times(1)).save(trainee);
    }

    @Test
    void testFindActiveTrainersNotAssignedToTrainee() {
        when(traineeRepository.findActiveTrainersNotAssignedToTrainee("testuser"))
                .thenReturn(Collections.singletonList(trainer));

        List<TrainerResponse> trainers = traineeService.findActiveTrainersNotAssignedToTrainee("testuser");

        assertNotNull(trainers);
        assertEquals(1, trainers.size());
        verify(traineeRepository, times(1)).findActiveTrainersNotAssignedToTrainee("testuser");
    }

    @Test
    void testGetTraineeTrainings() {
        LocalDate from = LocalDate.of(2023, 1, 1);
        LocalDate to = LocalDate.of(2023, 12, 31);

        Training training = new Training();
        training.setTrainingName("Test Training");
        training.setTrainingDate(LocalDate.now());
        training.setTrainer(trainer);

        when(trainingRepository.findAll(any(Specification.class))).thenReturn(Collections.singletonList(training));

        List<TraineeTrainingResponse> trainings = traineeService.getTraineeTrainings("testuser", from, to, null, null);

        assertNotNull(trainings);
        assertEquals(1, trainings.size());
        verify(trainingRepository, times(1)).findAll(any(Specification.class));
    }
}
