package com.epam.learn.springcore.dao;

import com.epam.learn.springcore.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TrainingRepository extends JpaRepository<Training, Integer>, JpaSpecificationExecutor<Training> {

}
