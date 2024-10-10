package com.epam.learn.springcore.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrainerResponse {
    private String username;
    private String firstName;
    private String lastName;
    private Integer specializationId;
}
