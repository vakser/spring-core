package com.epam.learn.springcore;

import com.epam.learn.springcore.dao.TraineeDAO;
import com.epam.learn.springcore.dao.TrainerDAO;
import com.epam.learn.springcore.dao.TrainingDAO;
import com.epam.learn.springcore.entity.Trainee;
import com.epam.learn.springcore.entity.Trainer;
import com.epam.learn.springcore.entity.Training;
import com.epam.learn.springcore.entity.TrainingType;
import com.epam.learn.springcore.service.TraineeService;
import com.epam.learn.springcore.service.TrainerService;
import com.epam.learn.springcore.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ServiceTests {
    @InjectMocks
    private TrainerService trainerService;
    @Mock
    private TrainerDAO trainerDAO;
    private final Map<String, Trainer> trainers = new HashMap<>();
    @InjectMocks
    private TraineeService traineeService;
    @Mock
    private TraineeDAO traineeDAO;
    private final Map<String, Trainee> trainees = new HashMap<>();
    @InjectMocks
    private TrainingService trainingService;
    @Mock
    private TrainingDAO trainingDAO;
    private final Map<String, Training> trainings = new HashMap<>();

    @BeforeEach
    public void setUp() {
        // Prepare mock data
        Trainer trainer = new Trainer();
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setUsername("John.Doe");
        trainer.setPassword("abcdefghij");
        trainer.setActive(true);
        trainer.setTrainingType(TrainingType.STRETCHING);
        trainers.put(trainer.getUsername(), trainer);
        Trainee trainee = new Trainee();
        trainee.setFirstName("Samantha");
        trainee.setLastName("Fox");
        trainee.setUsername("Samantha.Fox");
        trainee.setPassword("abcdefghij");
        trainee.setActive(true);
        trainee.setDateOfBirth(new Date());
        trainee.setAddress("111 Unknown st Unknown city TX 11111 USA");
        trainees.put(trainee.getUsername(), trainee);
        Training training = new Training();
        training.setTraineeUsername(trainee.getUsername());
        training.setTrainerUsername(trainer.getUsername());
        training.setTrainingName("Fitness training 3 times weekly");
        training.setTrainingType(TrainingType.FITNESS);
        training.setTrainingDate(new Date());
        training.setTrainingDuration(45);
        trainings.put(trainee.getUsername() + "-" + trainer.getUsername(), training);
        // Mock the UserDao methods
        when(trainerDAO.select(trainer.getUsername())).thenReturn(trainers.get(trainer.getUsername()));
        when(traineeDAO.select(trainee.getUsername())).thenReturn(trainees.get(trainee.getUsername()));
        when(trainingDAO.select(trainee.getUsername(), trainer.getUsername())).thenReturn(trainings.get(trainee.getUsername() + "-" + trainer.getUsername()));
    }

    @Test
    void testSelectTrainer() {
        Trainer trainer = trainerService.selectTrainer("John.Doe");
        assertNotNull(trainer);
        assertEquals("John", trainer.getFirstName());
        assertEquals("Doe", trainer.getLastName());
        assertTrue(trainer.isActive());
        assertEquals(TrainingType.STRETCHING, trainer.getTrainingType());
    }

    @Test
    void testCreateTrainer() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Mary");
        trainer.setLastName("Public");
        trainer.setUsername("Mary.Public");
        trainer.setPassword("abcdefghij");
        trainer.setActive(true);
        trainer.setTrainingType(TrainingType.ZUMBA);
        trainerService.createTrainer(trainer);
        // Verify that the DAO method create was called with the correct argument
        verify(trainerDAO, times(1)).create(trainer);
    }

    @Test
    void testUpdateTrainer() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setUsername("John.Doe");
        trainer.setPassword("abcdefghij");
        trainer.setActive(false);
        trainer.setTrainingType(TrainingType.YOGA);
        trainerService.updateTrainer(trainer);
        // Verify that the DAO method update was called with the correct argument
        verify(trainerDAO, times(1)).update(trainer);
    }

    @Test
    void testSelectTrainee() {
        Trainee trainee = traineeService.selectTrainee("Samantha.Fox");
        assertNotNull(trainee);
        assertEquals("Samantha", trainee.getFirstName());
        assertEquals("Fox", trainee.getLastName());
        assertTrue(trainee.isActive());
        assertEquals("111 Unknown st Unknown city TX 11111 USA", trainee.getAddress());
    }

    @Test
    void testCreateTrainee() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Mary");
        trainee.setLastName("Public");
        trainee.setUsername("Mary.Public");
        trainee.setPassword("abcdefghij");
        trainee.setActive(true);
        trainee.setDateOfBirth(new Date());
        trainee.setAddress("555 Where St Where City AL 55555 USA");
        traineeService.createTrainee(trainee);
        // Verify that the DAO method create was called with the correct argument
        verify(traineeDAO, times(1)).create(trainee);
    }

    @Test
    void testUpdateTrainee() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Samantha");
        trainee.setLastName("Fox");
        trainee.setUsername("Samantha.Fox");
        trainee.setPassword("abcdefghij");
        trainee.setActive(false);
        trainee.setDateOfBirth(new Date());
        trainee.setAddress("1 Shevchenka St Lviv 77777 Ukraine");
        traineeService.updateTrainee(trainee);
        // Verify that the DAO method update was called with the correct argument
        verify(traineeDAO, times(1)).update(trainee);
    }

    @Test
    void testDeleteTrainee() {
        traineeService.deleteTrainee("Samantha.Fox");
        // Verify that the DAO method delete was called with the correct argument
        verify(traineeDAO, times(1)).delete("Samantha.Fox");
    }

    @Test
    void testSelectTraining() {
        Training training = trainingService.selectTraining("Samantha.Fox", "John.Doe");
        assertNotNull(training);
        assertEquals("Samantha.Fox", training.getTraineeUsername());
        assertEquals("John.Doe", training.getTrainerUsername());
        assertEquals("Fitness training 3 times weekly", training.getTrainingName());
        assertEquals(TrainingType.FITNESS, training.getTrainingType());
        assertEquals(45, training.getTrainingDuration());
    }

    @Test
    void testCreateTraining() {
        Training training = new Training();
        training.setTraineeUsername("John.McGregor");
        training.setTrainerUsername("Alicia.McLough");
        training.setTrainingName("Weekly yoga training class");
        training.setTrainingType(TrainingType.YOGA);
        training.setTrainingDate(new Date());
        training.setTrainingDuration(90);
        trainingService.createTraining(training);
        // Verify that the DAO method create was called with the correct argument
        verify(trainingDAO, times(1)).create(training);
    }

}
