package com.epam.learn.springcore.dao;

import com.epam.learn.springcore.entity.Trainer;

public interface TrainerDAO {
    void create(Trainer trainer);
    void update(Trainer trainer);
    Trainer select(String username);

}
