package com.epam.learn.springcore.dao;

import com.epam.learn.springcore.entity.Trainee;

public interface TraineeDAO {
    void create(Trainee trainee);
    void update(Trainee trainee);
    void delete(String username);
    Trainee select(String username);
}
