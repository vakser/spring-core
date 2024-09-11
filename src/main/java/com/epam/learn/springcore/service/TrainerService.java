package com.epam.learn.springcore.service;

import com.epam.learn.springcore.dao.TrainerDAO;
import com.epam.learn.springcore.entity.Trainer;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class TrainerService {
    @Autowired
    private TrainerDAO trainerDAO;

    public void createTrainer(Trainer trainer) {
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

}
