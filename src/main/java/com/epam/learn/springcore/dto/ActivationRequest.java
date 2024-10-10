package com.epam.learn.springcore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivationRequest {
    @NotBlank(message = "Username is mandatory")
    private String username;
    @NotNull(message = "Status of user activation is mandatory")
    private Boolean isActive;
}
