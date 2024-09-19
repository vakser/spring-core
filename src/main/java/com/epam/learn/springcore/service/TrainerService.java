package com.epam.learn.springcore.service;

import com.epam.learn.springcore.dao.TrainerRepository;
import com.epam.learn.springcore.entity.Trainer;
import com.epam.learn.springcore.entity.Training;
import com.epam.learn.springcore.entity.User;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Log4j2
public class TrainerService {
    private final TrainerRepository trainerRepository;
    private final UserService userService;

    public TrainerService(TrainerRepository trainerRepository, UserService userService) {
        this.trainerRepository = trainerRepository;
        this.userService = userService;
    }

    @Transactional
    public void createTrainer(Trainer trainer) {
        User user = trainer.getUser();
        log.info("Calculating trainer username");
        user.setUsername(userService.calculateUsername(user.getFirstName(), user.getLastName()));
        log.info("Trainer username calculated");
        log.info("Generating trainer password");
        user.setPassword(userService.generateRandomPassword());
        log.info("Trainer password generated");
        log.info("Creating trainer: {}", user.getUsername());
//        userRepository.save(user);
        trainerRepository.save(trainer);
        log.info("Successfully created trainer: {}", user.getUsername());
    }

    @Transactional
    public void updateTrainer(Trainer trainer) {
        if (userService.authenticate(trainer.getUser().getUsername(), trainer.getUser().getPassword())) {
            log.info("Updating trainer: {}", trainer.getUser().getUsername());
            trainerRepository.save(trainer);
            log.info("Successfully updated trainer: {}", trainer.getUser().getUsername());
        } else {
            log.warn("No trainer {} found or username and password not matching", trainer.getUser().getUsername());
        }
    }

    public Trainer selectTrainer(String username, String password) {
        if (userService.authenticate(username, password)) {
            log.info("Selecting trainer: {}", username);
            return trainerRepository.findByUsername(username);
        } else {
            log.warn("No trainer {} found or username and password not matching", username);
            return null;
        }
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        if (userService.authenticate(username, oldPassword)) {
            log.info("Changing password for trainer: {}", username);
            Trainer trainer = trainerRepository.findByUsername(username);
            trainer.getUser().setPassword(newPassword);
            trainerRepository.save(trainer);
            log.info("Password for trainer {} changed", username);
        } else {
            log.warn("No trainer {} found or username and password not matching", username);
        }
    }

    @Transactional
    public void activateTrainer(Trainer trainer) {
        if (userService.authenticate(trainer.getUser().getUsername(), trainer.getUser().getPassword())) {
            log.info("Activating trainer: {}", trainer.getUser().getUsername());
            trainer.getUser().setIsActive(true);
            trainerRepository.save(trainer);
            log.info("Successfully activated trainer: {}", trainer.getUser().getUsername());
        } else {
            log.warn("No trainer {} found or username and password for the trainer not matching", trainer.getUser().getUsername());
        }
    }


    @Transactional
    public void deactivateTrainer(Trainer trainer) {
        if (userService.authenticate(trainer.getUser().getUsername(), trainer.getUser().getPassword())) {
            log.info("Deactivating trainer: {}", trainer.getUser().getUsername());
            trainer.getUser().setIsActive(false);
            trainerRepository.save(trainer);
            log.info("Successfully deactivated trainer: {}", trainer.getUser().getUsername());
        } else {
            log.warn("No trainer {} found or username and password for the trainer not matching", trainer.getUser().getUsername());
        }
    }

    public List<Training> getTrainerTrainingsByDateIntervalAndTraineeName(Trainer trainer, String traineeUsername,
                                                                          LocalDate fromDate, LocalDate toDate) {
        if (userService.authenticate(trainer.getUser().getUsername(), trainer.getUser().getPassword())) {
            log.info("Getting trainer {} trainings for trainee {} list from {} to {}",
                    trainer.getUser().getUsername(), traineeUsername, fromDate, toDate);
            return trainerRepository.findByDateIntervalAndTraineeName(traineeUsername, fromDate, toDate, trainer.getUser().getUsername());
        } else {
            log.warn("No trainer {} found or username and password for the trainer not matching", trainer.getUser().getUsername());
            return null;
        }
    }

}
