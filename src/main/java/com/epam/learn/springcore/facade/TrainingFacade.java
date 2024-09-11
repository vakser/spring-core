package com.epam.learn.springcore.facade;

import com.epam.learn.springcore.entity.Trainee;
import com.epam.learn.springcore.entity.Trainer;
import com.epam.learn.springcore.entity.Training;
import com.epam.learn.springcore.service.TraineeService;
import com.epam.learn.springcore.service.TrainerService;
import com.epam.learn.springcore.service.TrainingService;
import org.springframework.stereotype.Component;

@Component
public class TrainingFacade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public TrainingFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public boolean assignTrainingSession(Training training) {
        Trainee trainee = traineeService.selectTrainee(training.getTraineeUsername());
        Trainer trainer = trainerService.selectTrainer(training.getTrainerUsername());
        Training trainingInStorage = trainingService.selectTraining(training.getTraineeUsername(), training.getTrainerUsername());
        if (trainee != null && trainer != null && trainee.isActive() && trainer.isActive() && trainingInStorage == null) {
            trainingService.createTraining(training);
            return true;
        }
        return false;
    }
}
