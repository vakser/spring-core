package com.epam.learn.springcore.dao.impl;

import com.epam.learn.springcore.dao.TrainerDAO;
import com.epam.learn.springcore.entity.Trainer;
import com.epam.learn.springcore.storage.TrainerStorage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Log4j2
public class TrainerDAOImpl implements TrainerDAO {
    private TrainerStorage trainerStorage;

    @Autowired
    public void setTrainerStorage(TrainerStorage trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Override
    public void create(Trainer trainer) {
        log.info("Saving trainer to storage");
        trainerStorage.getStorage().put(trainer.getUsername(), trainer);
        log.info("Trainer saved to storage");
    }

    @Override
    public void update(Trainer trainer) {
        Trainer trainerToUpdate = trainerStorage.getStorage().get(trainer.getUsername());
        if (trainerToUpdate == null) {
            log.warn("Trainer not found in storage");
        } else {
            log.info("Updating trainer in storage");
            trainerStorage.getStorage().put(trainer.getUsername(), trainer);
            log.info("Trainer updated in storage");
        }
    }

    @Override
    public Trainer select(String username) {
        log.info("Searching for trainer {} in storage", username);
        if (!trainerStorage.getStorage().containsKey(username)) {
            log.warn("Trainer {} not found in storage", username);
        } else {
            log.info("Trainer {} was found in storage", username);
        }
        return trainerStorage.getStorage().get(username);
    }

}
