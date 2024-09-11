package com.epam.learn.springcore.storage;

import com.epam.learn.springcore.entity.Trainee;
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
public class TraineeStorage {
    private Map<String, Trainee> storage;

    @Value("${trainee.data.file.path}")
    private String traineeDataFilePath;

    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Trainee>> typeRef = new TypeReference<>() {
        };
        try {
            storage = mapper.readValue(new File(traineeDataFilePath), typeRef);
            log.info("Trainee data loaded");
        } catch (IOException e) {
            log.error("An error occurred while reading trainee data file", e);
        }
    }

}
