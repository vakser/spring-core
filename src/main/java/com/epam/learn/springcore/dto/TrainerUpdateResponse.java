package com.epam.learn.springcore.dto;

import com.epam.learn.springcore.entity.Trainee;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class TrainerUpdateResponse {
    private String username;
    private String firstName;
    private String lastName;
    private Integer specializationId;
    private Boolean isActive;
    private List<Trainee> traineesList;
}
