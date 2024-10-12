package com.epam.learn.springcore.service;

import com.epam.learn.springcore.dao.TraineeRepository;
import com.epam.learn.springcore.dao.TrainerRepository;
import com.epam.learn.springcore.dao.TrainingRepository;
import com.epam.learn.springcore.dto.*;
import com.epam.learn.springcore.entity.Trainee;
import com.epam.learn.springcore.entity.Trainer;
import com.epam.learn.springcore.entity.Training;
import com.epam.learn.springcore.entity.User;
import com.epam.learn.springcore.exception.TraineeNotFoundException;
import com.epam.learn.springcore.exception.TrainerNotFoundException;
import com.epam.learn.springcore.specification.TraineeTrainingSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class TraineeService {
    private final TraineeRepository traineeRepository;
    private final UserService userService;
    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;

    @Transactional
    public UserResponse createTrainee(TraineeRegistrationRequest traineeRegistrationRequest) {
        User user = new User();
        user.setFirstName(traineeRegistrationRequest.getFirstName());
        user.setLastName(traineeRegistrationRequest.getLastName());
        log.info("Calculating trainee username");
        user.setUsername(userService.calculateUsername(traineeRegistrationRequest.getFirstName(), traineeRegistrationRequest.getLastName()));
        log.info("Trainee username calculated: {}", user.getUsername());
        log.info("Generating trainee password");
        user.setPassword(userService.generateRandomPassword());
        log.info("Trainee password generated: {}", user.getPassword());
        user.setIsActive(false);
        log.info("Creating trainee: {}", user.getUsername());
        Trainee trainee = new Trainee();
        trainee.setDateOfBirth(traineeRegistrationRequest.getDateOfBirth());
        trainee.setAddress(traineeRegistrationRequest.getAddress());
        trainee.setUser(user);
        traineeRepository.save(trainee);
        log.info("Successfully created trainee: {}", user.getUsername());
        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(trainee.getUser().getUsername());
        userResponse.setPassword(trainee.getUser().getPassword());
        return userResponse;
    }

    @Transactional
    public TraineeUpdateResponse updateTrainee(TraineeUpdateRequest traineeUpdateRequest) {
        Trainee trainee = traineeRepository.findByUsername(traineeUpdateRequest.getUsername())
                .orElseThrow(() -> new TraineeNotFoundException("Trainee " + traineeUpdateRequest.getUsername() + " not found"));
        log.info("Updating trainee: {}", trainee.getUser().getUsername());
        trainee.getUser().setFirstName(traineeUpdateRequest.getFirstName());
        trainee.getUser().setLastName(traineeUpdateRequest.getLastName());
        trainee.setDateOfBirth(traineeUpdateRequest.getDateOfBirth());
        trainee.setAddress(traineeUpdateRequest.getAddress());
        Trainee updatedTrainee = traineeRepository.save(trainee);
        log.info("Successfully updated trainee: {}", trainee.getUser().getUsername());
        return convertTraineeToTraineeUpdateResponse(updatedTrainee);
    }

    public void deleteTrainee(String username) {
        log.info("Deleting trainee: {}", username);
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee " + username + " not found"));
        traineeRepository.delete(trainee);
        log.info("Successfully deleted trainee: {}", username);
    }

    public GetTraineeProfileResponse selectTrainee(String username) {
        log.info("Selecting trainee: {}", username);
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee " + username + " not found"));
        return convertTraineeToGetTraineeProfileResponse(trainee);
    }

    @Transactional
    public void changeTraineeActivationStatus(ActivationRequest activationRequest) {
        Trainee trainee = traineeRepository.findByUsername(activationRequest.getUsername())
                .orElseThrow(() -> new TraineeNotFoundException("Trainee " + activationRequest.getUsername() + " not found"));
        log.info("Changing activation status of the trainee: {}", trainee.getUser().getUsername());
        trainee.getUser().setIsActive(activationRequest.getIsActive());
        traineeRepository.save(trainee);
        log.info("Activation status changed successfully for the trainee: {}", trainee.getUser().getUsername());
    }

    public List<TrainerResponse> findActiveTrainersNotAssignedToTrainee(String username) {
        log.info("Searching active trainers not assigned to trainee: {}", username);
        return traineeRepository.findActiveTrainersNotAssignedToTrainee(username)
                .stream().map(this::convertTrainerToTrainerResponse).toList();
    }

    @Transactional
    public List<TrainerResponse> updateTraineeTrainers(UpdateTraineeTrainersRequest request) {
        Trainee trainee = traineeRepository.findByUsername(request.getTraineeUsername())
                .orElseThrow(() -> new TraineeNotFoundException("Trainee " + request.getTraineeUsername() + " not found"));
        List<String> trainersNames = request.getTrainerUsernames();
        List<Trainer> traineeTrainers = trainee.getTrainers();
        for (String trainerName : trainersNames) {
            Trainer trainer = trainerRepository.findByUsername(trainerName)
                    .orElseThrow(() -> new TrainerNotFoundException("Trainer " + trainerName + " not found"));
            if (!traineeTrainers.contains(trainer) && trainer.getUser().getIsActive()) {
                traineeTrainers.add(trainer);
            }
        }
        trainee.setTrainers(traineeTrainers);
        traineeRepository.save(trainee);
        return traineeTrainers.stream().map(this::convertTrainerToTrainerResponse).toList();
    }

    public List<TraineeTrainingResponse> getTraineeTrainings(String username, LocalDate periodFrom, LocalDate periodTo, String trainerName, String trainingType) {
        Specification<Training> spec = TraineeTrainingSpecification.trainingsByCriteria(username, periodFrom, periodTo, trainerName, trainingType);
        List<Training> trainings = trainingRepository.findAll(spec);
        return trainings.stream().map(this::convertTrainingToTraineeTrainingResponse).toList();
    }

    private TrainerResponse convertTrainerToTrainerResponse(Trainer trainer) {
        return  TrainerResponse.builder()
                .username(trainer.getUser().getUsername())
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .specializationId(trainer.getSpecialization().getId())
                .build();

    }

    private TraineeTrainingResponse convertTrainingToTraineeTrainingResponse(Training training) {
        return TraineeTrainingResponse.builder()
                .trainingName(training.getTrainingName())
                .trainingDate(training.getTrainingDate())
                .trainingType(training.getTrainer().getSpecialization().getName())
                .trainingDuration(training.getTrainingDuration())
                .trainerUsername(training.getTrainer().getUser().getUsername())
                .build();
    }

    private TraineeUpdateResponse convertTraineeToTraineeUpdateResponse(Trainee trainee) {
        return TraineeUpdateResponse.builder()
                .username(trainee.getUser().getUsername())
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .isActive(trainee.getUser().getIsActive())
                .trainersList(trainee.getTrainers())
                .build();
    }

    private GetTraineeProfileResponse convertTraineeToGetTraineeProfileResponse(Trainee trainee) {
        return GetTraineeProfileResponse.builder()
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .isActive(trainee.getUser().getIsActive())
                .trainersList(trainee.getTrainers().stream().map(this::convertTrainerToTrainerListResponse).toList())
                .build();
    }

    private TrainerListResponse convertTrainerToTrainerListResponse(Trainer trainer) {
        return TrainerListResponse.builder()
                .username(trainer.getUser().getUsername())
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .specializationId(trainer.getSpecialization().getId())
                .build();
    }

}
