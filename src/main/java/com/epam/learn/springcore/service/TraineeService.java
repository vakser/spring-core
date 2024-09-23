package com.epam.learn.springcore.service;

import com.epam.learn.springcore.dao.TraineeRepository;
import com.epam.learn.springcore.dao.UserRepository;
import com.epam.learn.springcore.entity.Trainee;
import com.epam.learn.springcore.entity.Trainer;
import com.epam.learn.springcore.entity.Training;
import com.epam.learn.springcore.entity.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Log4j2
public class TraineeService {
    private final TraineeRepository traineeRepository;
    private final UserService userService;

    public TraineeService(TraineeRepository traineeRepository, UserService userService) {
        this.traineeRepository = traineeRepository;
        this.userService = userService;
    }

    @Transactional
    public void createTrainee(Trainee trainee) {
        User user = trainee.getUser();
        log.info("Calculating trainee username");
        user.setUsername(userService.calculateUsername(user.getFirstName(), user.getLastName()));
        log.info("Trainee username calculated");
        log.info("Generating trainee password");
        user.setPassword(userService.generateRandomPassword());
        log.info("Trainee password generated");
        log.info("Creating trainee: {}", user.getUsername());
//        userRepository.save(user);
        traineeRepository.save(trainee);
        log.info("Successfully created trainee: {}", user.getUsername());
    }

    @Transactional
    public void updateTrainee(Trainee trainee) {
        if (userService.authenticate(trainee.getUser().getUsername(), trainee.getUser().getPassword())) {
            log.info("Updating trainee: {}", trainee.getUser().getUsername());
            traineeRepository.save(trainee);
            log.info("Successfully updated trainee: {}", trainee.getUser().getUsername());
        } else {
            log.warn("No trainee {} found or username and password not matching", trainee.getUser().getUsername());
        }
    }

    @Transactional
    public void deleteTrainee(String username, String password) {
        if (userService.authenticate(username, password)) {
            log.info("Deleting trainee: {}", username);
            traineeRepository.deleteByUsername(username);
            log.info("Successfully deleted trainee: {}", username);
        } else {
            log.warn("No trainee {} found or username and password for trainee not matching", username);
        }
    }

    public Trainee selectTrainee(String username, String password) {
        if (userService.authenticate(username, password)) {
            log.info("Selecting trainee: {}", username);
            return traineeRepository.findByUsername(username);
        } else {
            log.warn("No trainee {} found or username and password not matching", username);
            return null;
        }
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        if (userService.authenticate(username, oldPassword)) {
            log.info("Changing password for trainee: {}", username);
            Trainee trainee = traineeRepository.findByUsername(username);
            trainee.getUser().setPassword(newPassword);
            traineeRepository.save(trainee);
            log.info("Password for trainee {} changed", username);
        } else {
            log.warn("No trainee {} found or username and password not matching", username);
        }
    }

    @Transactional
    public void activateTrainee(Trainee trainee) {
        if (userService.authenticate(trainee.getUser().getUsername(), trainee.getUser().getPassword())) {
            log.info("Activating trainee: {}", trainee.getUser().getUsername());
            trainee.getUser().setIsActive(true);
            traineeRepository.save(trainee);
            log.info("Successfully activated trainee: {}", trainee.getUser().getUsername());
        } else {
            log.warn("No trainee {} found or username and password for the trainee not matching",
                    trainee.getUser().getUsername());
        }
    }


    @Transactional
    public void deactivateTrainee(Trainee trainee) {
        if (userService.authenticate(trainee.getUser().getUsername(), trainee.getUser().getPassword())) {
            log.info("Deactivating trainee: {}", trainee.getUser().getUsername());
            trainee.getUser().setIsActive(false);
            traineeRepository.save(trainee);
            log.info("Successfully deactivated trainee: {}", trainee.getUser().getUsername());
        } else {
            log.warn("No trainee {} found or username and password for the trainee not matching",
                    trainee.getUser().getUsername());
        }
    }

    public List<Training> getTraineeTrainingsByDateIntervalTrainerNameAndTrainingType(Trainee trainee, LocalDate fromDate,
                                                                                      LocalDate toDate, String trainerUsername,
                                                                                      String trainingType) {
        if (userService.authenticate(trainee.getUser().getUsername(), trainee.getUser().getPassword())) {
            log.info("Getting trainee {} trainings on {} by trainer {} from {} to {}",
                    trainee.getUser().getUsername(), trainingType, trainerUsername, fromDate, toDate);
            return traineeRepository.findByDateIntervalTrainerNameAndTrainingType(trainee.getUser().getUsername(),
                    fromDate, toDate, trainerUsername, trainingType);
        } else {
            log.warn("No trainee {} found or username and password for the trainee not matching",
                    trainee.getUser().getUsername());
            return null;
        }
    }

    public List<Trainer> getTrainersNotAssignedOnTrainee(Trainee trainee) {
        if (userService.authenticate(trainee.getUser().getUsername(), trainee.getUser().getPassword())) {
            log.info("Getting trainers list not assigned on trainee {}", trainee.getUser().getUsername());
            return traineeRepository.findTrainersNotAssignedOnTrainee(trainee.getUser().getUsername());
        } else {
            log.warn("No trainee {} found or username and password for the trainee not matching",
                    trainee.getUser().getUsername());
            return null;
        }
    }

}
