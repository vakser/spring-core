package com.epam.learn.springcore.dao;

import com.epam.learn.springcore.entity.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingTypeRepository extends JpaRepository<TrainingType, Integer> {
}
