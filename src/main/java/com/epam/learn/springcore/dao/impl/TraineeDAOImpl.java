package com.epam.learn.springcore.dao.impl;

import com.epam.learn.springcore.dao.TraineeDAO;
import com.epam.learn.springcore.entity.Trainee;
import com.epam.learn.springcore.storage.TraineeStorage;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Log4j2
public class TraineeDAOImpl implements TraineeDAO {
    private TraineeStorage traineeStorage;

    @Autowired
    public void setTraineeStorage(TraineeStorage traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Override
    public void create(Trainee trainee) {
        log.info("Calculating trainee username");
        trainee.setUsername(generateUsername(trainee.getFirstName(), trainee.getLastName()));
        log.info("Trainee username calculated");
        log.info("Generating trainee password");
        trainee.setPassword(generateRandomPassword());
        log.info("Trainee password generated");
        log.info("Saving trainer to storage");
        traineeStorage.getStorage().put(trainee.getUsername(), trainee);
        log.info("Trainer saved to storage");
    }

    @Override
    public void update(Trainee trainee) {
        Trainee traineeToUpdate = traineeStorage.getStorage().get(trainee.getUsername());
        if (traineeToUpdate == null) {
            log.warn("Trainee not found");
        } else {
            log.info("Updating trainee in storage");
            traineeStorage.getStorage().put(trainee.getUsername(), trainee);
            log.info("Trainee updated in storage");
        }
    }

    @Override
    public Trainee select(String username) {
        log.info("Searching for trainee {} in storage", username);
        if (!traineeStorage.getStorage().containsKey(username)) {
            log.warn("Trainee {} not found in storage", username);
        } else {
            log.info("Trainee {} was found in storage", username);
        }
        return traineeStorage.getStorage().get(username);
    }

    @Override
    public void delete(String username) {
        if (!traineeStorage.getStorage().containsKey(username)) {
            log.warn("No trainee {} found in storage", username);
        } else {
            log.info("Deleting trainee {} from storage", username);
            traineeStorage.getStorage().remove(username);
            log.info("Trainee {} deleted from storage", username);
        }
    }

    private String generateUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        int count = 1;
        String finalUsername = baseUsername;
        while (select(finalUsername) != null) {
            finalUsername = baseUsername + count;
            count++;
        }
        return finalUsername;
    }

    private String generateRandomPassword() {
        return RandomStringUtils.secure().next(10);
    }
}
