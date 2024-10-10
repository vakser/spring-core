package com.epam.learn.springcore.dao;

import com.epam.learn.springcore.entity.Training;
import com.epam.learn.springcore.specification.TraineeTrainingSearchCriteria;
import com.epam.learn.springcore.specification.TraineeTrainingSpecification;
import com.epam.learn.springcore.specification.TrainerTrainingSearchCriteria;
import com.epam.learn.springcore.specification.TrainerTrainingSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Integer>, JpaSpecificationExecutor<Training> {

    default List<Training> findByCriteria(TraineeTrainingSearchCriteria criteria) {
        return findAll(new TraineeTrainingSpecification(criteria));
    }

    default List<Training> findByCriteria(TrainerTrainingSearchCriteria criteria) {
        return findAll(new TrainerTrainingSpecification(criteria));
    }

}
