package com.epam.learn.springcore.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GetTrainerProfileResponse {
    private String firstName;
    private String lastName;
    private Integer specializationId;
    private Boolean isActive;
    private List<TraineeListResponse> traineesList;
}
