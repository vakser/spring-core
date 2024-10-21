package com.epam.learn.springcore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.epam.learn.springcore.dao.TraineeRepository;
import com.epam.learn.springcore.dao.TrainerRepository;
import com.epam.learn.springcore.dao.TrainingRepository;
import com.epam.learn.springcore.dao.TrainingTypeRepository;
import com.epam.learn.springcore.dto.*;
import com.epam.learn.springcore.entity.*;
import com.epam.learn.springcore.exception.TrainingTypeNotFoundException;
import com.epam.learn.springcore.service.TrainerService;
import com.epam.learn.springcore.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrainerServiceTest {
    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserService userService;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @InjectMocks
    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTrainer_successfulCreation() {
        // Arrange
        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setSpecializationId(1);

        TrainingType trainingType = new TrainingType();
        trainingType.setId(1);

        User user = new User();
        user.setUsername("John.Doe");
        user.setPassword("randomPassword");
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(trainingType);

        when(userService.calculateUsername("John", "Doe")).thenReturn("John.Doe");
        when(userService.generateRandomPassword()).thenReturn("randomPassword");
        when(trainingTypeRepository.findById(1)).thenReturn(Optional.of(trainingType));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);

        // Act
        UserResponse result = trainerService.createTrainer(request);

        // Assert
        assertThat(result.getUsername()).isEqualTo("John.Doe");
        assertThat(result.getPassword()).isEqualTo("randomPassword");
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void createTrainer_throwsTrainingTypeNotFoundException() {
        // Arrange
        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setSpecializationId(1);

        when(trainingTypeRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TrainingTypeNotFoundException.class, () -> trainerService.createTrainer(request));

        verify(trainerRepository, never()).save(any(Trainer.class));
    }

    @Test
    void updateTrainer_successfulUpdate() {
        // Arrange
        TrainerUpdateRequest request = new TrainerUpdateRequest();
        request.setUsername("John.Doe");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setSpecializationId(1);

        TrainingType trainingType = new TrainingType();
        trainingType.setId(1);

        User user = new User();
        user.setUsername("John.Doe");
        user.setPassword("randomPassword");
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(trainingType);

        when(trainerRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);

        // Act
        TrainerUpdateResponse result = trainerService.updateTrainer(request);

        // Assert
        assertThat(result.getUsername()).isEqualTo("John.Doe");
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void selectTrainer_successfulSelection() {
        // Arrange
        TrainingType trainingType = new TrainingType();
        trainingType.setId(1);

        User user = new User();
        user.setUsername("John.Doe");
        user.setPassword("randomPassword");
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(trainingType);
        trainer.setTrainees(new ArrayList<>());

        when(trainerRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainer));

        // Act
        GetTrainerProfileResponse result = trainerService.selectTrainer("John.Doe");

        // Assert
        assertThat(result.getFirstName()).isEqualTo(user.getFirstName());
        verify(trainerRepository).findByUsername("John.Doe");
    }

    @Test
    void changeTrainerActivationStatus_successfulChange() {
        // Arrange
        ActivationRequest request = new ActivationRequest();
        request.setUsername("John.Doe");
        request.setIsActive(true);

        Trainer trainer = new Trainer();
        User user = new User();
        user.setUsername("John.Doe");
        trainer.setUser(user);

        when(trainerRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainer));

        // Act
        trainerService.changeTrainerActivationStatus(request);

        // Assert
        assertThat(trainer.getUser().getIsActive()).isTrue();
        verify(trainerRepository).save(trainer);
    }

    @Test
    void getTrainerTrainings_success() {
        // Arrange
        String username = "jdoe";
        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.now().plusDays(7);
        Trainer trainer = new Trainer();
        trainer.setSpecialization(new TrainingType());
        Trainee trainee = new Trainee();
        User user = new User();
        user.setUsername("Mary.Public");
        user.setFirstName("Mary");
        user.setLastName("Public");
        trainee.setUser(user);

        List<Training> trainings = new ArrayList<>();
        Training training = new Training();
        training.setTrainingName("Cardio");
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        trainings.add(training);

        when(trainingRepository.findAll(any(Specification.class))).thenReturn(trainings);

        // Act
        List<TrainerTrainingResponse> result = trainerService.getTrainerTrainings(username, from, to, null);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTrainingName()).isEqualTo("Cardio");
        verify(trainingRepository).findAll(any(Specification.class));
    }

    @Test
    void addTraining_successfulAdd() {
        // Arrange
        AddTrainingRequest request = new AddTrainingRequest();
        request.setTraineeUsername("trainee1");
        request.setTrainerUsername("trainer1");
        request.setTrainingName("Cardio");
        request.setTrainingDate(LocalDate.now());

        Trainee trainee = new Trainee();
        User traineeUser = new User();
        traineeUser.setUsername("trainee1");
        trainee.setUser(traineeUser);

        Trainer trainer = new Trainer();
        User trainerUser = new User();
        trainerUser.setUsername("trainer1");
        trainer.setUser(trainerUser);
        trainer.setTrainees(List.of(trainee));

        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(trainingRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        trainerService.addTraining(request);

        // Assert
        verify(trainingRepository).save(any(Training.class));
    }
}
