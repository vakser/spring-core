package com.epam.learn.springcore.storage;

import com.epam.learn.springcore.entity.Trainer;
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
public class TrainerStorage {
    private Map<String, Trainer> storage;

    @Value("${trainer.data.file.path}")
    private String trainerDataFilePath;

    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Trainer>> typeRef = new TypeReference<>() {
        };
        try {
            storage = mapper.readValue(new File(trainerDataFilePath), typeRef);
            log.info("Trainer data loaded");
        } catch (IOException e) {
            log.error("An error occurred while reading trainer data file", e);
        }
    }

}
