package com.epam.learn.springcore;

import com.epam.learn.springcore.dao.TraineeDAO;
import com.epam.learn.springcore.dao.TrainerDAO;
import com.epam.learn.springcore.dao.TrainingDAO;
import com.epam.learn.springcore.entity.Trainee;
import com.epam.learn.springcore.entity.Trainer;
import com.epam.learn.springcore.entity.Training;
import com.epam.learn.springcore.entity.TrainingType;
import com.epam.learn.springcore.storage.TraineeStorage;
import com.epam.learn.springcore.storage.TrainerStorage;
import com.epam.learn.springcore.storage.TrainingStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StorageAndDAOTests {
    @Autowired
    private TrainerStorage trainerStorage;
    @Autowired
    private TrainerDAO trainerDAO;
    @Autowired
    private TraineeStorage traineeStorage;
    @Autowired
    private TraineeDAO traineeDAO;
    @Autowired
    private TrainingStorage trainingStorage;
    @Autowired
    private TrainingDAO trainingDAO;

    @BeforeEach
    void setUp() {
        trainerStorage.init();
        traineeStorage.init();
        trainingStorage.init();
    }

    @Test
    void testStoragesInitialized() {
        assertNotNull(trainerDAO.select("Sergii.Vakaliuk"));
        assertNull(trainerDAO.select("Charles.Johnson"));
        assertNotNull(traineeDAO.select("John.Doe"));
        assertNull(traineeDAO.select("Mike.Tyson"));
        assertNotNull(trainingDAO.select("John.Doe", "Sergii.Vakaliuk"));
        assertNull(trainingDAO.select("Mike.Tyson", "Sergii.Vakaliuk"));
    }

    @Test
    void testCreateTrainer() {
        assertEquals(trainerStorage.getStorage().size(), 1);
        Trainer trainer = Trainer.builder()
                .firstName("Sven")
                .lastName("Nielsen")
                .username("Sven.Nielsen")
                .isActive(true)
                .trainingType(TrainingType.STRETCHING)
                .build();
        trainerDAO.create(trainer);
        assertEquals(trainerStorage.getStorage().size(), 2);
        assertNotNull(trainerDAO.select("Sven.Nielsen"));
    }

    @Test
    void testUpdateTrainer() {
        assertTrue(trainerDAO.select("Sergii.Vakaliuk").isActive());
        assertEquals(TrainingType.FITNESS, trainerDAO.select("Sergii.Vakaliuk").getTrainingType());
        Trainer trainer = trainerDAO.select("Sergii.Vakaliuk");
        trainer.setActive(false);
        trainer.setTrainingType(TrainingType.YOGA);
        trainerDAO.update(trainer);
        assertFalse(trainerDAO.select("Sergii.Vakaliuk").isActive());
        assertEquals(trainerDAO.select("Sergii.Vakaliuk").getTrainingType(), TrainingType.YOGA);
    }

    @Test
    void testCreateTrainee() {
        assertEquals(2, traineeStorage.getStorage().size());
        Trainee trainee = Trainee.builder()
                .firstName("Charles")
                .lastName("Benson")
                .username("Charles.Benson")
                .isActive(true)
                .dateOfBirth(new Date())
                .address("321 Some St Sometown CA 54321 USA")
                .build();
        traineeDAO.create(trainee);
        assertEquals(3, traineeStorage.getStorage().size());
        assertNotNull(traineeDAO.select("Charles.Benson"));
    }

    @Test
    void testUpdateTrainee() {
        assertTrue(traineeDAO.select("Mary.Public").isActive());
        assertEquals("123 Main St Anytown CA 12345 USA", traineeDAO.select("Mary.Public").getAddress());
        Trainee trainee = traineeDAO.select("Mary.Public");
        trainee.setActive(false);
        trainee.setAddress("11 Independence Street Ivano-Frankivsk  11111 Ukraine");
        traineeDAO.update(trainee);
        assertFalse(traineeDAO.select("Mary.Public").isActive());
        assertEquals("11 Independence Street Ivano-Frankivsk  11111 Ukraine", traineeDAO.select("Mary.Public").getAddress());
    }

    @Test
    void testDeleteTrainee() {
        assertEquals(2, traineeStorage.getStorage().size());
        traineeDAO.delete("Mary.Public");
        assertNull(traineeDAO.select("Mary.Public"));
        assertEquals(1, traineeStorage.getStorage().size());
    }

    @Test
    void testCreateTraining() {
        assertEquals(1, trainingStorage.getStorage().size());
        Training training = Training.builder()
                .traineeUsername("Mary.Public")
                .trainerUsername("Sergii.Vakaliuk")
                .trainingName("Fitness training 3 times weekly")
                .trainingType(TrainingType.FITNESS)
                .trainingDate(new Date())
                .trainingDuration(45)
                .build();
        trainingDAO.create(training);
        assertEquals(2, trainingStorage.getStorage().size());
        assertNotNull(trainingDAO.select("Mary.Public", "Sergii.Vakaliuk"));
    }

}
