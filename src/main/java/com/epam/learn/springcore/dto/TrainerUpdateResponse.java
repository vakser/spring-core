package com.epam.learn.springcore.dto;

import com.epam.learn.springcore.entity.Trainee;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerUpdateResponse {
    private String username;
    private String firstName;
    private String lastName;
    private Integer specializationId;
    private Boolean isActive;
    private List<Trainee> traineesList;
}
