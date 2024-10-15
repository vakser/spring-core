package com.epam.learn.springcore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    @NotBlank(message = "Username is mandatory")
    private String username;
    @NotBlank(message = "Old password is mandatory")
    private String oldPassword;
    @NotBlank(message = "New password is mandatory")
    private String newPassword;
}
