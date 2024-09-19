package com.epam.learn.springcore.dao;

import com.epam.learn.springcore.entity.Trainee;
import com.epam.learn.springcore.entity.Trainer;
import com.epam.learn.springcore.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TraineeRepository extends JpaRepository<Trainee, Integer> {
    @Query("SELECT t FROM Trainee t WHERE t.user.username = :username")
    Trainee findByUsername(@Param("username") String username);

    @Modifying
    @Query("DELETE FROM Trainee t WHERE t.user.username = :username")
    void deleteByUsername(@Param("username") String username);

    @Query("SELECT t FROM Training t JOIN t.trainee tr JOIN t.trainer tn WHERE " +
            "tr.user.username = :traineeUsername AND t.trainingDate BETWEEN :fromDate AND :toDate AND " +
            "tn.user.username = :trainerUsername AND t.trainingType = :trainingType")
    List<Training> findByDateIntervalTrainerNameAndTrainingType(String traineeUsername, LocalDate fromDate,
                                                                      LocalDate toDate, String trainerUsername,
                                                                      String trainingType);

    @Query("SELECT t FROM Trainer t WHERE t.user.username NOT IN (SELECT tr.trainer.user.username FROM Training tr " +
            "WHERE tr.trainee.user.username = :traineeUsername)")
    List<Trainer> findTrainersNotAssignedOnTrainee(@Param("traineeUsername") String traineeUsername);

}
