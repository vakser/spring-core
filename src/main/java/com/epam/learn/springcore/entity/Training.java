package com.epam.learn.springcore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trainee_id")
    private Trainee trainee;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;
    @Column(nullable = false)
    private String trainingName;
    // should it be date and time for the case several trainings are planned for the same trainer on the same date???
    @Column(nullable = false)
    private LocalDate trainingDate;
    @Column(nullable = false)
    private Integer trainingDuration;
}
