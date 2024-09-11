package com.epam.learn.springcore.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.Date;

@Getter
@Setter
@SuperBuilder
@Jacksonized
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Trainee extends User {
    @JsonProperty
    private Date dateOfBirth;
    @JsonProperty
    private String address;
}
