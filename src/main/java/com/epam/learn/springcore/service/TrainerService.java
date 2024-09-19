package com.epam.learn.springcore.service;

import com.epam.learn.springcore.dao.TrainerDAO;
import com.epam.learn.springcore.entity.Trainer;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class TrainerService {
    @Autowired
    private TrainerDAO trainerDAO;

    public void createTrainer(Trainer trainer) {
        log.info("Calculating trainer username");
        trainer.setUsername(calculateUsername(trainer.getFirstName(), trainer.getLastName()));
        log.info("Trainer username calculated");
        log.info("Generating trainer password");
        trainer.setPassword(generateRandomPassword());
        log.info("Trainer password generated");
        log.info("Creating trainer: {}", trainer);
        trainerDAO.create(trainer);
        log.info("Successfully created trainer: {}", trainer);
    }

    public void updateTrainer(Trainer trainer) {
        Trainer trainerToUpdate = trainerDAO.select(trainer.getUsername());
        if (trainerToUpdate == null) {
            log.warn("No trainer found with username: {}", trainer.getUsername());
        } else {
            log.info("Updating trainer: {}", trainerToUpdate);
            trainerDAO.update(trainer);
            log.info("Successfully updated trainer: {}", trainer);
        }
    }

    public Trainer selectTrainer(String username) {
        log.info("Selecting trainer: {}", username);
        return trainerDAO.select(username);
    }

    private String calculateUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        int count = 1;
        String finalUsername = baseUsername;
        while (trainerDAO.select(finalUsername) != null) {
            finalUsername = baseUsername + count;
            count++;
        }
        return finalUsername;
    }

    private String generateRandomPassword() {
        return RandomStringUtils.secure().next(10);
    }

}
