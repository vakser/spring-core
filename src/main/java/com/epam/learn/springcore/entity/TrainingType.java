package com.epam.learn.springcore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
public class TrainingType {
    @Id
    private Integer id;
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Name is mandatory")
    private String name;
//    @OneToMany(mappedBy = "trainingType", fetch = FetchType.LAZY)
//    private List<Training> trainings;
//    @OneToMany(mappedBy = "specialization", fetch = FetchType.LAZY)
//    private List<Trainer> trainers;
}
