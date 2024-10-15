package com.epam.learn.springcore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TrainerUpdateRequest {
    @NotBlank(message = "Username is mandatory")
    private String username;
    @NotBlank(message = "First name is mandatory")
    private String firstName;
    @NotBlank(message = "Last name is mandatory")
    private String lastName;
    private Integer specializationId;
    @NotNull(message = "Status of activation is mandatory")
    private Boolean isActive;
}
