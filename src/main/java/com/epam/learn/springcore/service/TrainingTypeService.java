package com.epam.learn.springcore.service;

import com.epam.learn.springcore.dao.TrainingTypeRepository;
import com.epam.learn.springcore.entity.TrainingType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class TrainingTypeService {
    private final TrainingTypeRepository trainingTypeRepository;

    public List<TrainingType> getAllTrainingTypes() {
        log.info("Fetching all training types");
        return trainingTypeRepository.findAll();
    }
}
