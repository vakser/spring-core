package com.epam.learn.springcore.service;

import com.epam.learn.springcore.dao.TrainingRepository;
import com.epam.learn.springcore.entity.Training;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class TrainingService {
    private final TrainingRepository trainingRepository;
    private final UserService userService;

    public TrainingService(TrainingRepository trainingRepository, UserService userService) {
        this.trainingRepository = trainingRepository;
        this.userService = userService;
    }

    public void addTraining(Training training, String password) {
        if (userService.authenticate(training.getTrainee().getUser().getUsername(), password)) {
            log.info("Adding new training: {}", training);
            trainingRepository.save(training);
            log.info("Successfully added training: {}", training);
        } else {
            log.warn("No trainee {} found or username and password not matching",
                    training.getTrainee().getUser().getUsername());
        }
    }

}
