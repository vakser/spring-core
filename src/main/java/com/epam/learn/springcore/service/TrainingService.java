package com.epam.learn.springcore.service;

import com.epam.learn.springcore.dao.TrainingDAO;
import com.epam.learn.springcore.entity.Training;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class TrainingService {
    @Autowired
    private TrainingDAO trainingDAO;

    public void createTraining(Training training) {
        log.info("Creating training: {}", training);
        trainingDAO.create(training);
        log.info("Successfully created training: {}", training);
    }

    public Training selectTraining(String traineeUsername, String trainerUsername) {
        log.info("Selecting training for trainee {} and trainer {}", traineeUsername, trainerUsername);
        return trainingDAO.select(traineeUsername, trainerUsername);
    }
}
