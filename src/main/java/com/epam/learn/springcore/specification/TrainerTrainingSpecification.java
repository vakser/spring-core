package com.epam.learn.springcore.specification;

import com.epam.learn.springcore.entity.Trainee;
import com.epam.learn.springcore.entity.Trainer;
import com.epam.learn.springcore.entity.Training;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TrainerTrainingSpecification {
    public static Specification<Training> trainingsByCriteria(String username, LocalDate periodFrom, LocalDate periodTo, String traineeName) {
        return (root, query, criteriaBuilder) -> {
            // Join Trainee entity to filter by username
            Join<Training, Trainer> trainerJoin = root.join("trainer", JoinType.INNER);
            Join<Training, Trainee> traineeJoin = root.join("trainee", JoinType.INNER);

            // Predicate to combine conditions
            Predicate predicate = criteriaBuilder.conjunction();

            // Filter by Trainer username
            if (username != null && !username.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(trainerJoin.get("user").get("username"), username));
            }

            // Filter by training period (from date)
            if (periodFrom != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("trainingDate"), periodFrom));
            }

            // Filter by training period (to date)
            if (periodTo != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("trainingDate"), periodTo));
            }

            // Filter by traineeName
            if (traineeName != null && !traineeName.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(traineeJoin.get("user").get("username"), traineeName));
            }

            return predicate;
        };
    }
}
