package com.epam.learn.springcore.specification;

import com.epam.learn.springcore.entity.Trainee;
import com.epam.learn.springcore.entity.Trainer;
import com.epam.learn.springcore.entity.Training;
import com.epam.learn.springcore.entity.TrainingType;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class TraineeTrainingSpecification implements Specification<Training> {
    private final TraineeTrainingSearchCriteria criteria;

    @Override
    public Predicate toPredicate(Root<Training> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        Join<Training, Trainee> traineeJoin = root.join("trainee");
        Join<Training, Trainer> trainerJoin = root.join("trainer");
        if (criteria.getUsername() != null) {
            predicates.add(criteriaBuilder.equal(traineeJoin.get("user").get("username"), criteria.getUsername()));
        }
        if (criteria.getDateFrom() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("trainingDate"), criteria.getDateFrom()));
        }
        if (criteria.getDateTo() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("trainingDate"), criteria.getDateTo()));
        }
        if (criteria.getTrainerName() != null) {
            predicates.add(criteriaBuilder.equal(trainerJoin.get("user").get("username"), criteria.getTrainerName()));
        }
        if (criteria.getTrainingType() != null) {
            Join<Trainer, TrainingType> trainingTypeJoin = trainerJoin.join("specialization");
            predicates.add(criteriaBuilder.equal(trainingTypeJoin.get("name"), criteria.getTrainingType()));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
