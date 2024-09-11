package com.epam.learn.springcore.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Training {
    private String traineeUsername;
    private String trainerUsername;
    private String trainingName;
    private TrainingType trainingType;
    private Date trainingDate;
    private int trainingDuration;
}
