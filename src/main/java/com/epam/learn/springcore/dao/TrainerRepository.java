package com.epam.learn.springcore.dao;

import com.epam.learn.springcore.entity.Trainer;
import com.epam.learn.springcore.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TrainerRepository extends JpaRepository<Trainer, Integer> {
    @Query("SELECT t FROM Trainer t WHERE t.user.username = :username")
    Trainer findByUsername(@Param("username")String username);

    @Query("SELECT t FROM Training t JOIN t.trainee tr JOIN t.trainer tn WHERE " +
            "tr.user.username = :traineeUsername AND t.trainingDate BETWEEN :fromDate AND :toDate AND " +
            "tn.user.username = :trainerUsername")
    List<Training> findByDateIntervalAndTraineeName(String traineeUsername, LocalDate fromDate,
                                                             LocalDate toDate, String trainerUsername);

}
