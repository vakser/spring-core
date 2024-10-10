package com.epam.learn.springcore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraineeUpdateRequest {
    @NotBlank(message = "Username is mandatory")
    private String username;
    @NotBlank(message = "First name is mandatory")
    private String firstName;
    @NotBlank(message = "Last name is mandatory")
    private String lastName;
    private LocalDate dateOfBirth;
    private String address;
    @NotNull(message = "Status of activation is mandatory")
    private Boolean isActive;
}
