package com.epam.learn.springcore.dto;

import com.epam.learn.springcore.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainerRequest {
    private Integer specializationId;
    private User user;
}
