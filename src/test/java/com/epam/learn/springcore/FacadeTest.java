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

    @Test
    void testAssignTrainingSession() {
        Trainee trainee1 = new Trainee();
        trainee1.setFirstName("John");
        trainee1.setLastName("Doe");
        trainee1.setUsername("John.Doe");
        trainee1.setPassword("1a2b3c4d5e");
        trainee1.setActive(true);
        trainee1.setDateOfBirth(new Date());
        trainee1.setAddress("111 Unknown st Unknown city TX 11111 USA");
        Trainee trainee2 = new Trainee();
        trainee2.setFirstName("Mary");
        trainee2.setLastName("Public");
        trainee2.setUsername("Mary.Public");
        trainee2.setPassword("1a2b3c4d5e");
        trainee2.setActive(false);
        trainee2.setDateOfBirth(new Date());
        trainee2.setAddress("1 Franka st Ivano-Frankivsk 11111 Ukraine");
        Trainer trainer = new Trainer();
        trainer.setFirstName("Sergii");
        trainer.setLastName("Vakaliuk");
        trainer.setUsername("Sergii.Vakaliuk");
        trainer.setPassword("1a2b3c4d5e");
        trainer.setActive(true);
        trainer.setTrainingType(TrainingType.STRETCHING);
        Training training1 = new Training();
        training1.setTraineeUsername(trainee1.getUsername());
        training1.setTrainerUsername(trainer.getUsername());
        training1.setTrainingName("Quads stretching training");
        training1.setTrainingType(TrainingType.STRETCHING);
        training1.setTrainingDate(new Date());
        training1.setTrainingDuration(45);
        Training training2 = new Training();
        training2.setTraineeUsername(trainee2.getUsername());
        training2.setTrainerUsername(trainer.getUsername());
        training2.setTrainingName("Hamstrings stretching training");
        training2.setTrainingType(TrainingType.STRETCHING);
        training2.setTrainingDate(new Date());
        training2.setTrainingDuration(60);
        when(traineeService.selectTrainee(trainee1.getUsername())).thenReturn(trainee1);
        when(traineeService.selectTrainee(trainee2.getUsername())).thenReturn(trainee2);
        when(trainerService.selectTrainer(trainer.getUsername())).thenReturn(trainer);
        boolean assignTrainingSession1 = trainingFacade.assignTrainingSession(training1);
        boolean assignTrainingSession2 = trainingFacade.assignTrainingSession(training2);
        assertTrue(assignTrainingSession1);
        assertFalse(assignTrainingSession2);
        when(trainingService.selectTraining(trainee1.getUsername(), trainer.getUsername())).thenReturn(training1);
        boolean assignTrainingSessionWhichWasAlreadyAssigned = trainingFacade.assignTrainingSession(training1);
        assertFalse(assignTrainingSessionWhichWasAlreadyAssigned);
    }
}
