package com.epam.learn.springcore.specification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TraineeTrainingSearchCriteria {
    private String username;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String trainerName;
    private String trainingType;
}
