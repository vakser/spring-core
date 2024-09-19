package com.epam.learn.springcore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "trainee_id")
    private Trainee trainee;
    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;
    @ManyToOne
    @JoinColumn(name = "training_type_id")
    private TrainingType trainingType;
    @Column(nullable = false)
    @NotBlank(message = "Training name is mandatory")
    private String trainingName;
    @Column(nullable = false)
    @NotBlank(message = "Training date is mandatory")
    private LocalDate trainingDate;
    @Column(nullable = false)
    @NotBlank(message = "Training duration is mandatory")
    private Integer trainingDuration;
}
