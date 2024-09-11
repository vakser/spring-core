package com.epam.learn.springcore.dao;

import com.epam.learn.springcore.entity.Training;

public interface TrainingDAO {
    void create(Training training);
    Training select(String traineeUsername, String trainerUsername);
}
