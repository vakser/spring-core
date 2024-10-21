package com.epam.learn.springcore.dto;

import com.epam.learn.springcore.entity.Trainer;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraineeUpdateResponse {
    private String username;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String address;
    private Boolean isActive;
    private List<Trainer> trainersList;
}
