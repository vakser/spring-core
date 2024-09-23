package com.epam.learn.springcore.controller;

import com.epam.learn.springcore.dao.TrainingTypeRepository;
import com.epam.learn.springcore.dto.TrainerRequest;
import com.epam.learn.springcore.entity.Trainer;
import com.epam.learn.springcore.entity.TrainingType;
import com.epam.learn.springcore.service.TrainerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainer")
public class TrainerController {
    private final TrainerService trainerService;
    private final TrainingTypeRepository trainingTypeRepository;

    public TrainerController(TrainerService trainerService, TrainingTypeRepository trainingTypeRepository) {
        this.trainerService = trainerService;
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @GetMapping("/{username}/password/{password}")
    public ResponseEntity<Trainer> getTrainer(@PathVariable String username, @PathVariable String password) {
        Trainer trainer = trainerService.selectTrainer(username, password);
        return ResponseEntity.ok(trainer);
    }

    @PostMapping()
    public ResponseEntity<String> addTrainer(@RequestBody TrainerRequest trainerRequest) {
        TrainingType trainingType = trainingTypeRepository.getReferenceById(trainerRequest.getSpecializationId());
        Trainer trainer = new Trainer();
        trainer.setSpecialization(trainingType);
        trainer.setUser(trainerRequest.getUser());
        trainerService.createTrainer(trainer);
        return ResponseEntity.ok("Trainer added successfully");
    }
}
