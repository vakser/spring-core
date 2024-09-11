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
        Trainer trainer = new Trainer();
        trainer.setFirstName("Sven");
        trainer.setLastName("Nielsen");
        trainer.setActive(true);
        trainer.setTrainingType(TrainingType.STRETCHING);
        trainerDAO.create(trainer);
        assertEquals(trainerStorage.getStorage().size(), 2);
        assertNotNull(trainerDAO.select("Sven.Nielsen"));
    }

    @Test
    void testCreateTrainerWithSameUsername() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Sergii");
        trainer.setLastName("Vakaliuk");
        trainer.setActive(true);
        trainer.setTrainingType(TrainingType.STRETCHING);
        trainerDAO.create(trainer);
        assertNotNull(trainerDAO.select("Sergii.Vakaliuk1"));
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
        Trainee trainee = new Trainee();
        trainee.setFirstName("Charles");
        trainee.setLastName("Benson");
        trainee.setActive(true);
        trainee.setDateOfBirth(new Date());
        trainee.setAddress("321 Some St Sometown CA 54321 USA");
        traineeDAO.create(trainee);
        assertEquals(3, traineeStorage.getStorage().size());
        assertNotNull(traineeDAO.select("Charles.Benson"));
    }

    @Test
    void testCreateTraineesWithSameUsername() {
        Trainee trainee1 = new Trainee();
        trainee1.setFirstName("Mary");
        trainee1.setLastName("Public");
        trainee1.setActive(true);
        trainee1.setDateOfBirth(new Date());
        trainee1.setAddress("555 Anywhere St Anytown CO 55555 USA");
        traineeDAO.create(trainee1);
        assertNotNull(traineeDAO.select("Mary.Public1"));
        Trainee trainee2 = new Trainee();
        trainee2.setFirstName("Mary");
        trainee2.setLastName("Public");
        trainee2.setActive(false);
        trainee2.setDateOfBirth(new Date());
        trainee2.setAddress("222 Somewhere St Itown JO 22222 USA");
        traineeDAO.create(trainee2);
        assertNotNull(traineeDAO.select("Mary.Public2"));
        assertEquals(4, traineeStorage.getStorage().size());
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
        Training training = new Training();
        training.setTraineeUsername("Mary.Public");
        training.setTrainerUsername("Sergii.Vakaliuk");
        training.setTrainingName("Fitness training 3 times weekly");
        training.setTrainingType(TrainingType.FITNESS);
        training.setTrainingDate(new Date());
        training.setTrainingDuration(45);
        trainingDAO.create(training);
        assertEquals(2, trainingStorage.getStorage().size());
        assertNotNull(trainingDAO.select("Mary.Public", "Sergii.Vakaliuk"));
    }

}
