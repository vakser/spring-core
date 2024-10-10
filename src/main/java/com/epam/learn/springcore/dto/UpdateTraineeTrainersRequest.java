package com.epam.learn.springcore.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateTraineeTrainersRequest {
    private String traineeUsername;
    @NotNull(message = "List with trainers usernames is mandatory")
    private List<String> trainerUsernames;
}
