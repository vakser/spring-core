package com.epam.learn.springcore;

import com.epam.learn.springcore.entity.Trainee;
import com.epam.learn.springcore.entity.Trainer;
import com.epam.learn.springcore.entity.Training;
import com.epam.learn.springcore.entity.TrainingType;
import com.epam.learn.springcore.facade.TrainingFacade;
import com.epam.learn.springcore.service.TraineeService;
import com.epam.learn.springcore.service.TrainerService;
import com.epam.learn.springcore.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
public class FacadeTest {
    @Mock
    private TrainerService trainerService;
    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainingService trainingService;
    @InjectMocks
    private TrainingFacade trainingFacade;

//    @Test
//    void testAssignTrainingSession() {
//        Trainee trainee1 = Trainee.builder()
//                .firstName("John")
//                .lastName("Doe")
//                .username("John.Doe")
//                .password("1a2b3c4d5e")
//                .isActive(true)
//                .dateOfBirth(new Date())
//                .address("111 Unknown st Unknown city TX 11111 USA")
//                .build();
//        Trainee trainee2 = Trainee.builder()
//                .firstName("Mary")
//                .lastName("Public")
//                .username("Mary.Public")
//                .password("1a2b3c4d5e")
//                .isActive(false)
//                .dateOfBirth(new Date())
//                .address("1 Franka st Ivano-Frankivsk 11111 Ukraine")
//                .build();
//        Trainer trainer = Trainer.builder()
//                .firstName("Sergii")
//                .lastName("Vakaliuk")
//                .username("Sergii.Vakaliuk")
//                .password("1a2b3c4d5e")
//                .isActive(true)
//                .trainingType(TrainingType.STRETCHING)
//                .build();
//        Training training1 = Training.builder()
//                .traineeUsername(trainee1.getUsername())
//                .trainerUsername(trainer.getUsername())
//                .trainingName("Quads stretching training")
//                .trainingType(TrainingType.STRETCHING)
//                .trainingDate(new Date())
//                .trainingDuration(45)
//                .build();
//        Training training2 = Training.builder()
//                .traineeUsername(trainee2.getUsername())
//                .trainerUsername(trainer.getUsername())
//                .trainingName("Hamstrings stretching training")
//                .trainingType(TrainingType.STRETCHING)
//                .trainingDate(new Date())
//                .trainingDuration(60)
//                .build();
//        when(traineeService.selectTrainee(trainee1.getUsername())).thenReturn(trainee1);
//        when(traineeService.selectTrainee(trainee2.getUsername())).thenReturn(trainee2);
//        when(trainerService.selectTrainer(trainer.getUsername())).thenReturn(trainer);
//        boolean assignTrainingSession1 = trainingFacade.assignTrainingSession(training1);
//        boolean assignTrainingSession2 = trainingFacade.assignTrainingSession(training2);
//        assertTrue(assignTrainingSession1);
//        assertFalse(assignTrainingSession2);
//        when(trainingService.selectTraining(trainee1.getUsername(), trainer.getUsername())).thenReturn(training1);
//        boolean assignTrainingSessionWhichWasAlreadyAssigned = trainingFacade.assignTrainingSession(training1);
//        assertFalse(assignTrainingSessionWhichWasAlreadyAssigned);
//    }
}
