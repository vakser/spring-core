package com.epam.learn.springcore.dao;

import com.epam.learn.springcore.entity.Trainee;
import com.epam.learn.springcore.entity.Trainer;
import com.epam.learn.springcore.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Integer> {
//    List<Trainer> findUnassignedTrainers(String traineeUsername);
//    void updateTrainersListForTrainee(String traineeUsername, List<Trainer> trainers);
    Training findByTraineeAndTrainer(Trainee trainee, Trainer trainer);
}
