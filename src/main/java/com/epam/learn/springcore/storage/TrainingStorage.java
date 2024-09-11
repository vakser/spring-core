package com.epam.learn.springcore.storage;

import com.epam.learn.springcore.entity.Training;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Getter
@Setter
@Log4j2
public class TrainingStorage {
    private Map<String, Training> storage;

    @Value("${training.data.file.path}")
    private String trainingDataFilePath;

    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Training>> typeRef = new TypeReference<>() {
        };
        try {
            storage = mapper.readValue(new File(trainingDataFilePath), typeRef);
            log.info("Training data loaded");
        } catch (IOException e) {
            log.error("An error occurred while reading training data file", e);
        }
    }
}
