package com.epam.learn.springcore.service;

import com.epam.learn.springcore.dao.TraineeRepository;
import com.epam.learn.springcore.dao.TrainerRepository;
import com.epam.learn.springcore.dao.TrainingRepository;
import com.epam.learn.springcore.dao.TrainingTypeRepository;
import com.epam.learn.springcore.dto.*;
import com.epam.learn.springcore.entity.*;
import com.epam.learn.springcore.exception.TraineeNotFoundException;
import com.epam.learn.springcore.exception.TrainerNotFoundException;
import com.epam.learn.springcore.exception.TrainingTypeNotFoundException;
import com.epam.learn.springcore.specification.TrainerTrainingSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class TrainerService {
    private final TrainerRepository trainerRepository;
    private final UserService userService;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createTrainer(TrainerRegistrationRequest trainerRegistrationRequest) {
        User user = new User();
        user.setFirstName(trainerRegistrationRequest.getFirstName());
        user.setLastName(trainerRegistrationRequest.getLastName());
        log.info("Calculating trainer username");
        user.setUsername(userService.calculateUsername(trainerRegistrationRequest.getFirstName(), trainerRegistrationRequest.getLastName()));
        log.info("Trainer username calculated: {}", user.getUsername());
        log.info("Generating trainer password");
        String password = userService.generateRandomPassword();
        user.setPassword(passwordEncoder.encode(password));
        log.info("Trainer password generated: {}", password);
        user.setIsActive(false);
        log.info("Creating trainer: {}", user.getUsername());
        Trainer trainer = new Trainer();
        TrainingType specialization = trainingTypeRepository.findById(trainerRegistrationRequest.getSpecializationId())
                .orElseThrow(() -> new TrainingTypeNotFoundException("Specialization type with id " + trainerRegistrationRequest.getSpecializationId() + " not found"));
        trainer.setSpecialization(specialization);
        trainer.setUser(user);
        trainerRepository.save(trainer);
        log.info("Successfully created trainer: {}", user.getUsername());
        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(trainer.getUser().getUsername());
        userResponse.setPassword(password);
        return userResponse;
    }

    @Transactional
    public TrainerUpdateResponse updateTrainer(TrainerUpdateRequest trainerUpdateRequest) {
        Trainer trainer = trainerRepository.findByUsername(trainerUpdateRequest.getUsername())
                .orElseThrow(() -> new TrainerNotFoundException("Trainer " + trainerUpdateRequest.getUsername() + " not found"));
        log.info("Updating trainer: {}", trainer.getUser().getUsername());
        trainer.getUser().setFirstName(trainerUpdateRequest.getFirstName());
        trainer.getUser().setLastName(trainerUpdateRequest.getLastName());
        Trainer updatedTrainer = trainerRepository.save(trainer);
        log.info("Successfully updated trainer: {}", trainer.getUser().getUsername());
        return convertTrainerToTrainerUpdateResponse(updatedTrainer);
    }

    public GetTrainerProfileResponse selectTrainer(String username) {
        log.info("Selecting trainer: {}", username);
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer " + username + " not found"));
        return convertTrainerToGetTrainerProfileResponse(trainer);
    }

    @Transactional
    public void changeTrainerActivationStatus(ActivationRequest activationRequest) {
        Trainer trainer = trainerRepository.findByUsername(activationRequest.getUsername())
                .orElseThrow(() -> new TrainerNotFoundException("Trainer " + activationRequest.getUsername() + " not found"));
        log.info("Changing activation status of the trainer: {}", trainer.getUser().getUsername());
        trainer.getUser().setIsActive(activationRequest.getIsActive());
        trainerRepository.save(trainer);
        log.info("Activation status changed successfully for the trainer: {}", trainer.getUser().getUsername());
    }

    public List<TrainerTrainingResponse> getTrainerTrainings(String username, LocalDate periodFrom, LocalDate periodTo, String traineeName) {
        Specification<Training> spec = TrainerTrainingSpecification.trainingsByCriteria(username, periodFrom, periodTo, traineeName);
        List<Training> trainings = trainingRepository.findAll(spec);
        return trainings.stream().map(this::convertTrainingToTrainerTrainingResponse).toList();
    }

    @Transactional
    public void addTraining(AddTrainingRequest addTrainingRequest) {
        log.info("Adding new training: {}", addTrainingRequest.getTrainingName());
        Trainee trainee = traineeRepository.findByUsername(addTrainingRequest.getTraineeUsername())
                .orElseThrow(() -> new TraineeNotFoundException("Trainee " + addTrainingRequest.getTraineeUsername() + " not found"));
        Trainer trainer = trainerRepository.findByUsername(addTrainingRequest.getTrainerUsername())
                .orElseThrow(() -> new TrainerNotFoundException("Trainer " + addTrainingRequest.getTrainerUsername() + " not found"));
        if (!trainer.getTrainees().contains(trainee)) {
            log.warn("The trainee {} have not requested the training by the trainer {}", trainee.getUser().getUsername(), trainer.getUser().getUsername());
            return;
        }
        List<Training> trainerTrainings = trainingRepository.findAll();
        for (Training training : trainerTrainings) {
            if (training.getTrainingDate().equals(addTrainingRequest.getTrainingDate())) {
                log.warn("There is already a training {} with trainee {} scheduled for the same date {}",
                        training.getTrainingName(), trainee.getUser().getUsername(), training.getTrainingDate());
                return;
            }
            // ask if local date and time supposed to be used instead of just local date ???
//            if (training.getTrainingDate().plus(training.getTrainingDuration(), ChronoUnit.MINUTES).isAfter(addTrainingRequest.getTrainingDate())) {
//                log.warn("The requested trainee training overlapping with already assigned training");
//                return;
//            }
        }
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName(addTrainingRequest.getTrainingName());
        training.setTrainingDate(addTrainingRequest.getTrainingDate());
        training.setTrainingDuration(addTrainingRequest.getTrainingDuration());
        trainingRepository.save(training);
        log.info("Successfully added training: {}", training);
    }

    private TrainerTrainingResponse convertTrainingToTrainerTrainingResponse(Training training) {
        return TrainerTrainingResponse.builder()
                .trainingName(training.getTrainingName())
                .trainingDate(training.getTrainingDate())
                .trainingType(training.getTrainer().getSpecialization().getName())
                .trainingDuration(training.getTrainingDuration())
                .traineeUsername(training.getTrainee().getUser().getUsername())
                .build();
    }

    private TrainerUpdateResponse convertTrainerToTrainerUpdateResponse(Trainer trainer) {
        return TrainerUpdateResponse.builder()
                .username(trainer.getUser().getUsername())
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .specializationId(trainer.getSpecialization().getId())
                .isActive(trainer.getUser().getIsActive())
                .traineesList(trainer.getTrainees())
                .build();
    }

    private GetTrainerProfileResponse convertTrainerToGetTrainerProfileResponse(Trainer trainer) {
        return GetTrainerProfileResponse.builder()
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .specializationId(trainer.getSpecialization().getId())
                .isActive(trainer.getUser().getIsActive())
                .traineesList(trainer.getTrainees().stream().map(this::convertTraineeToTraineeListResponse).toList())
                .build();
    }

    private TraineeListResponse convertTraineeToTraineeListResponse(Trainee trainee) {
        return TraineeListResponse.builder()
                .username(trainee.getUser().getUsername())
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .build();
    }

}
