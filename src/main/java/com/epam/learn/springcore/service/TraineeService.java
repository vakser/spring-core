package com.epam.learn.springcore.service;

import com.epam.learn.springcore.dao.TraineeDAO;
import com.epam.learn.springcore.entity.Trainee;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class TraineeService {
    @Autowired
    private TraineeDAO traineeDAO;

    public void createTrainee(Trainee trainee) {
        log.info("Creating trainee: {}", trainee);
        traineeDAO.create(trainee);
        log.info("Successfully created trainee: {}", trainee);
    }

    public void updateTrainee(Trainee trainee) {
        if (traineeDAO.select(trainee.getUsername()) == null) {
            log.warn("No trainee found: {}", trainee.getUsername());
        } else {
            traineeDAO.update(trainee);
            log.info("Successfully updated trainee: {}", trainee.getUsername());
        }
    }

    public void deleteTrainee(String username) {
        log.info("Deleting trainee: {}", username);
        if (traineeDAO.select(username) == null) {
            log.warn("No trainee found for username: {}", username);
        } else {
            traineeDAO.delete(username);
            log.info("Successfully deleted trainee: {}", username);
        }
    }

    public Trainee selectTrainee(String username) {
        log.info("Selecting trainee: {}", username);
        return traineeDAO.select(username);
    }

}
