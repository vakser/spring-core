package com.epam.learn.springcore.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TrainerListResponse {
    private String username;
    private String firstName;
    private String lastName;
    private Integer specializationId;
}
