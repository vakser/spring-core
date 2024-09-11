package com.epam.learn.springcore.dao.impl;

import com.epam.learn.springcore.dao.TrainingDAO;
import com.epam.learn.springcore.entity.Training;
import com.epam.learn.springcore.storage.TrainingStorage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Log4j2
public class TrainingDAOImpl implements TrainingDAO {
    private TrainingStorage trainingStorage;

    @Autowired
    public void setTrainingStorage(TrainingStorage trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Override
    public void create(Training training) {
        log.info("Saving training to storage");
        trainingStorage.getStorage().put(training.getTraineeUsername() + "-" + training.getTrainerUsername(), training);
        log.info("Training saved to storage");
    }

    @Override
    public Training select(String traineeUsername, String trainerUsername) {
        log.info("Retrieving training from storage");
        if (!trainingStorage.getStorage().containsKey(traineeUsername + "-" + trainerUsername)) {
            log.warn("Training for trainee {} and trainer {} not found in storage", traineeUsername, trainerUsername);
        } else {
            log.info("Training was found in storage");
        }
        return trainingStorage.getStorage().get(traineeUsername + "-" + trainerUsername);
    }

}
