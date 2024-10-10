package com.epam.learn.springcore;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.epam.learn.springcore.dao.TraineeRepository;
import com.epam.learn.springcore.dao.TrainerRepository;
import com.epam.learn.springcore.dao.TrainingRepository;
import com.epam.learn.springcore.dto.*;
import com.epam.learn.springcore.entity.*;
import com.epam.learn.springcore.exception.TraineeNotFoundException;
import com.epam.learn.springcore.exception.TrainerNotFoundException;
import com.epam.learn.springcore.service.TraineeService;
import com.epam.learn.springcore.service.UserService;
import com.epam.learn.springcore.specification.TraineeTrainingSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class TraineeServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainingRepository trainingRepository;
    @InjectMocks
    private TraineeService traineeService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTrainee() {
        // Arrange
        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));
        request.setAddress("123 Main St");

        // Mock UserService behavior
        when(userService.calculateUsername("John", "Doe")).thenReturn("John.Doe");
        when(userService.generateRandomPassword()).thenReturn("randomPassword123");

        // Act
        UserResponse response = traineeService.createTrainee(request);

        // Assert
        assertNotNull(response);
        assertEquals("John.Doe", response.getUsername());
        assertEquals("randomPassword123", response.getPassword());

        // Verifying that the methods in UserService and TraineeRepository were called correctly
        verify(userService, times(1)).calculateUsername("John", "Doe");
        verify(userService, times(1)).generateRandomPassword();
        verify(traineeRepository, times(1)).save(any(Trainee.class));
    }

    @Test
    void testUpdateTraineeSuccess() {
        // Arrange
        TraineeUpdateRequest request = new TraineeUpdateRequest();
        request.setUsername("John.Doe");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setAddress("123 Main St");

        User user = new User();
        user.setUsername("John.Doe");
        user.setFirstName("OldFirstName");
        user.setLastName("OldLastName");

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(LocalDate.of(1980, 12, 1));
        trainee.setAddress("Old Address");

        // Mock repository findByUsername
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));

        // Mock repository save
        when(traineeRepository.save(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TraineeUpdateResponse response = traineeService.updateTrainee(request);

        // Assert
        assertNotNull(response);
        assertEquals("John", trainee.getUser().getFirstName());
        assertEquals("Doe", trainee.getUser().getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), trainee.getDateOfBirth());
        assertEquals("123 Main St", trainee.getAddress());

        // Verify repository interactions
        verify(traineeRepository, times(1)).findByUsername("John.Doe");
        verify(traineeRepository, times(1)).save(trainee);
    }

    @Test
    void testUpdateTraineeNotFound() {
        // Arrange
        TraineeUpdateRequest request = new TraineeUpdateRequest();
        request.setUsername("unknownUser");

        when(traineeRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TraineeNotFoundException.class, () -> traineeService.updateTrainee(request));

        // Verify repository interactions
        verify(traineeRepository, times(1)).findByUsername("unknownUser");
        verify(traineeRepository, never()).save(any(Trainee.class));
    }

    @Test
    void testDeleteTraineeSuccess() {
        // Arrange
        String username = "John.Doe";

        // Mock an existing trainee
        User user = new User();
        user.setUsername(username);

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        // Mock the repository findByUsername method to return the existing trainee
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));

        // Act
        traineeService.deleteTrainee(username);

        // Assert & Verify
        verify(traineeRepository, times(1)).findByUsername(username);
        verify(traineeRepository, times(1)).delete(trainee);
    }

    @Test
    void testDeleteTraineeNotFound() {
        // Arrange
        String username = "unknownUser";

        // Mock the repository findByUsername to return an empty Optional
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        TraineeNotFoundException exception = assertThrows(TraineeNotFoundException.class, () -> traineeService.deleteTrainee(username));

        // Assert Exception Message
        assertEquals("Trainee unknownUser not found", exception.getMessage());

        // Verify
        verify(traineeRepository, times(1)).findByUsername(username);
        verify(traineeRepository, never()).delete(any(Trainee.class)); // Ensure delete is not called
    }

    @Test
    void testSelectTraineeSuccess() {
        // Arrange
        String username = "John.Doe";

        // Mock a Trainee with the given username
        User user = new User();
        user.setUsername(username);
        user.setFirstName("John");
        user.setLastName("Doe");

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setAddress("123 Main St");
        trainee.setTrainers(new ArrayList<>());

        // Mock the repository findByUsername method to return the existing trainee
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));

        // Mock the conversion method to return a GetTraineeProfileResponse
        GetTraineeProfileResponse expectedResponse = new GetTraineeProfileResponse();
        expectedResponse.setFirstName("John");
        expectedResponse.setLastName("Doe");
        expectedResponse.setDateOfBirth(LocalDate.of(1990, 1, 1));
        expectedResponse.setAddress("123 Main St");
        expectedResponse.setIsActive(true);
        expectedResponse.setTrainersList(new ArrayList<>());

        // Act
        GetTraineeProfileResponse response = traineeService.selectTrainee(username);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse.getFirstName(), response.getFirstName());
        assertEquals(expectedResponse.getLastName(), response.getLastName());
        assertEquals(expectedResponse.getDateOfBirth(), response.getDateOfBirth());
        assertEquals(expectedResponse.getAddress(), response.getAddress());

        // Verify repository interactions
        verify(traineeRepository, times(1)).findByUsername(username);
    }

    @Test
    void testSelectTraineeTraineeNotFound() {
        // Arrange
        String username = "unknownUser";

        // Mock the repository findByUsername method to return an empty Optional
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        TraineeNotFoundException exception = assertThrows(TraineeNotFoundException.class, () -> traineeService.selectTrainee(username));

        // Assert the exception message
        assertEquals("Trainee unknownUser not found", exception.getMessage());

        // Verify repository interactions
        verify(traineeRepository, times(1)).findByUsername(username);
    }

    @Test
    void testChangeTraineeActivationStatusSuccess() {
        // Arrange
        String username = "John.Doe";
        boolean isActive = true; // New activation status

        // Mock a Trainee with the given username
        User user = new User();
        user.setUsername(username);
        user.setIsActive(false); // Initial status

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        // Mock the repository findByUsername method to return the existing trainee
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));

        // Mock the save method to return the updated trainee
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        // Create the activation request
        ActivationRequest activationRequest = new ActivationRequest();
        activationRequest.setUsername(username);
        activationRequest.setIsActive(isActive);

        // Act
        traineeService.changeTraineeActivationStatus(activationRequest);

        // Assert
        assertEquals(isActive, trainee.getUser().getIsActive(), "The trainee's activation status should be updated");

        // Verify repository interactions
        verify(traineeRepository, times(1)).findByUsername(username);
        verify(traineeRepository, times(1)).save(trainee);
    }

    @Test
    void testChangeTraineeActivationStatusNotFound() {
        // Arrange
        String username = "unknownUser";
        boolean isActive = true; // Activation status

        // Mock the repository findByUsername method to return an empty Optional
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Create the activation request
        ActivationRequest activationRequest = new ActivationRequest();
        activationRequest.setUsername(username);
        activationRequest.setIsActive(isActive);

        // Act & Assert
        TraineeNotFoundException exception = assertThrows(TraineeNotFoundException.class, () -> traineeService.changeTraineeActivationStatus(activationRequest));

        // Assert the exception message
        assertEquals("Trainee unknownUser not found", exception.getMessage());

        // Verify repository interactions
        verify(traineeRepository, times(1)).findByUsername(username);
        verify(traineeRepository, never()).save(any(Trainee.class)); // Ensure save is never called
    }

    @Test
    void testFindActiveTrainersNotAssignedToTraineeSuccess() {
        // Arrange
        String username = "John.Doe";

        // Mock a list of trainers
        List<Trainer> trainers = new ArrayList<>();
        User user1 = new User(1, "John", "Doe", "John.Doe", "password123", true);
        User user2 = new User(2, "Mary", "Public", "Mary.Public", "password321", true);
        Trainer trainer1 = new Trainer();
        trainer1.setUser(user1);
        trainer1.setSpecialization(new TrainingType());
        Trainer trainer2 = new Trainer();
        trainer2.setUser(user2);
        trainer2.setSpecialization(new TrainingType());
        trainers.add(trainer1);
        trainers.add(trainer2);

        // Mock the repository method to return the list of trainers
        when(traineeRepository.findActiveTrainersNotAssignedToTrainee(username)).thenReturn(trainers);

        // Act
        List<TrainerResponse> result = traineeService.findActiveTrainersNotAssignedToTrainee(username);

        // Assert
        assertEquals(2, result.size(), "The result should contain two trainers");

        // Verify repository interactions
        verify(traineeRepository, times(1)).findActiveTrainersNotAssignedToTrainee(username);
    }

    @Test
    void testFindActiveTrainersNotAssignedToTraineeEmptyList() {
        // Arrange
        String username = "John.Doe";

        // Mock an empty list of trainers
        List<Trainer> trainers = new ArrayList<>();

        // Mock the repository method to return an empty list
        when(traineeRepository.findActiveTrainersNotAssignedToTrainee(username)).thenReturn(trainers);

        // Act
        List<TrainerResponse> result = traineeService.findActiveTrainersNotAssignedToTrainee(username);

        // Assert
        assertTrue(result.isEmpty(), "The result should be an empty list");

        // Verify repository interactions
        verify(traineeRepository, times(1)).findActiveTrainersNotAssignedToTrainee(username);
    }

    @Test
    void testUpdateTraineeTrainersSuccess() {

        // Create a mock trainee with no trainers
        Trainee trainee = new Trainee();
        User user = new User(1, "John", "Doe", "John.Doe", "password123", true);
        trainee.setUser(user);
        trainee.setTrainers(new ArrayList<>());

        // Mock the trainee repository to return the trainee
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));

        // Create a mock trainer
        Trainer trainer = new Trainer();
        trainer.setSpecialization(new TrainingType());
        trainer.setUser(new User(2, "Mary", "Public", "Mary.Public", "password123", true));

        // Mock the trainer repository to return the trainer
        when(trainerRepository.findByUsername("Mary.Public")).thenReturn(Optional.of(trainer));

        // Create the update request with trainer's username
        UpdateTraineeTrainersRequest request = new UpdateTraineeTrainersRequest();
        request.setTraineeUsername("John.Doe");
        request.setTrainerUsernames(List.of("Mary.Public"));

        // Act
        List<TrainerResponse> result = traineeService.updateTraineeTrainers(request);

        // Assert
        assertEquals(1, result.size(), "The result should contain one trainer response");

        // Verify repository interactions
        verify(traineeRepository, times(1)).findByUsername("John.Doe");
        verify(trainerRepository, times(1)).findByUsername("Mary.Public");
        verify(traineeRepository, times(1)).save(trainee);
    }

    @Test
    void testUpdateTraineeTrainersTraineeNotFound() {
        // Arrange
        String traineeUsername = "unknownTrainee";
        UpdateTraineeTrainersRequest request = new UpdateTraineeTrainersRequest();
        request.setTraineeUsername(traineeUsername);
        request.setTrainerUsernames(List.of("trainer1"));

        // Mock the trainee repository to return an empty Optional
        when(traineeRepository.findByUsername(traineeUsername)).thenReturn(Optional.empty());

        // Act & Assert
        TraineeNotFoundException exception = assertThrows(TraineeNotFoundException.class, () -> traineeService.updateTraineeTrainers(request));

        // Assert the exception message
        assertEquals("Trainee unknownTrainee not found", exception.getMessage());

        // Verify repository interactions
        verify(traineeRepository, times(1)).findByUsername(traineeUsername);
        verify(trainerRepository, never()).findByUsername(anyString()); // Ensure trainer lookup is never called
    }

    @Test
    void testUpdateTraineeTrainersTrainerNotFound() {
        // Arrange
        String traineeUsername = "trainee1";
        String trainerUsername = "unknownTrainer";

        // Create a mock trainee with no trainers
        Trainee trainee = new Trainee();
        trainee.setTrainers(new ArrayList<>());

        // Mock the trainee repository to return the trainee
        when(traineeRepository.findByUsername(traineeUsername)).thenReturn(Optional.of(trainee));

        // Mock the trainer repository to return an empty Optional
        when(trainerRepository.findByUsername(trainerUsername)).thenReturn(Optional.empty());

        // Create the update request with an unknown trainer's username
        UpdateTraineeTrainersRequest request = new UpdateTraineeTrainersRequest();
        request.setTraineeUsername(traineeUsername);
        request.setTrainerUsernames(List.of(trainerUsername));

        // Act & Assert
        TrainerNotFoundException exception = assertThrows(TrainerNotFoundException.class, () -> traineeService.updateTraineeTrainers(request));

        // Assert the exception message
        assertEquals("Trainer unknownTrainer not found", exception.getMessage());

        // Verify repository interactions
        verify(traineeRepository, times(1)).findByUsername(traineeUsername);
        verify(trainerRepository, times(1)).findByUsername(trainerUsername);
    }

    @Test
    void testUpdateTraineeTrainers_InactiveTrainer() {
        // Arrange
        String traineeUsername = "trainee1";
        String trainerUsername = "inactiveTrainer";

        // Create a mock trainee with no trainers
        Trainee trainee = new Trainee();
        trainee.setTrainers(new ArrayList<>());

        // Mock the trainee repository to return the trainee
        when(traineeRepository.findByUsername(traineeUsername)).thenReturn(Optional.of(trainee));

        // Create a mock trainer
        Trainer trainer = new Trainer();
        trainer.setUser(new User());
        trainer.getUser().setIsActive(false); // Set the trainer as inactive

        // Mock the trainer repository to return the inactive trainer
        when(trainerRepository.findByUsername(trainerUsername)).thenReturn(Optional.of(trainer));

        // Create the update request with the inactive trainer's username
        UpdateTraineeTrainersRequest request = new UpdateTraineeTrainersRequest();
        request.setTraineeUsername(traineeUsername);
        request.setTrainerUsernames(List.of(trainerUsername));

        // Act
        List<TrainerResponse> result = traineeService.updateTraineeTrainers(request);

        // Assert
        assertTrue(result.isEmpty(), "The result should be an empty list as the trainer is inactive");

        // Verify repository interactions
        verify(traineeRepository, times(1)).findByUsername(traineeUsername);
        verify(trainerRepository, times(1)).findByUsername(trainerUsername);
        verify(traineeRepository, times(1)).save(trainee);
    }

    @Test
    void testGetTraineeTrainings() {
        // Arrange
        TraineeTrainingSearchCriteria criteria = new TraineeTrainingSearchCriteria();

        // Mocking a list of Training objects as returned by the repository
        Trainer trainer1 = new Trainer(1, new TrainingType(1, "Yoga"), new ArrayList<>(), new ArrayList<>(), new User(1, "John", "Doe", "John.Doe", "password", true));
        Trainer trainer2 = new Trainer(2, new TrainingType(2, "Fitness"), new ArrayList<>(), new ArrayList<>(), new User(2, "Mary", "Public", "Mary.Public", "password", true));
        List<Training> mockTrainings = new ArrayList<>();
        mockTrainings.add(new Training(1, new Trainee(), trainer1, "Yoga for old people", LocalDate.of(2024, 10, 1), 60));
        mockTrainings.add(new Training(2, new Trainee(), trainer2, "MMA fitness", LocalDate.of(2024, 11, 1), 90));

        // Mock the repository call
        when(trainingRepository.findByCriteria(criteria)).thenReturn(mockTrainings);

        // Expected conversion result (after converting Training to TraineeTrainingResponse)
        List<TraineeTrainingResponse> expectedResponses = new ArrayList<>();
        expectedResponses.add(new TraineeTrainingResponse( "Yoga for old people", LocalDate.of(2024, 10, 1), "Yoga", 60, "John.Doe"));
        expectedResponses.add(new TraineeTrainingResponse("MMA fitness", LocalDate.of(2024, 11, 1), "Fitness", 90, "Mary.Public"));

        // Act
        List<TraineeTrainingResponse> actualResponses = traineeService.getTraineeTrainings(criteria);

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size());
        assertEquals(expectedResponses.get(0).getTrainingName(), actualResponses.get(0).getTrainingName());
        assertEquals(expectedResponses.get(0).getTrainingDate(), actualResponses.get(0).getTrainingDate());
        assertEquals(expectedResponses.get(1).getTrainingName(), actualResponses.get(1).getTrainingName());
        assertEquals(expectedResponses.get(1).getTrainingDate(), actualResponses.get(1).getTrainingDate());

        // Verify that the repository method was called exactly once with the provided criteria
        verify(trainingRepository, times(1)).findByCriteria(criteria);
    }

}
