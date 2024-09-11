package com.epam.learn.springcore.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@SuperBuilder
@Jacksonized
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Trainer extends User {
    @JsonProperty
    private TrainingType trainingType;
}
