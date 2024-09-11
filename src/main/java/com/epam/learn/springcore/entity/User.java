package com.epam.learn.springcore.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@SuperBuilder
@Jacksonized
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties("password")
public class User {
    protected String firstName;
    protected String lastName;
    protected String username;
    protected String password;
    protected boolean isActive;

}
