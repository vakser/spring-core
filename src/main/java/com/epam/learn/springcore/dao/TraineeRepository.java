package com.epam.learn.springcore.dao;

import com.epam.learn.springcore.entity.Trainee;
import com.epam.learn.springcore.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Integer> {
    @Query("SELECT t FROM Trainee t WHERE t.user.username = :username")
    Optional<Trainee> findByUsername(@Param("username") String username);

    @Query("""
            SELECT t FROM Trainer t\s
            WHERE t.user.isActive = true\s
            AND t.id NOT IN (SELECT tr.id FROM Trainee te JOIN te.trainers tr\s
            WHERE te.user.username = :username)""")
    List<Trainer> findActiveTrainersNotAssignedToTrainee(@Param("username") String username);

}
