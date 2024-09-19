package com.epam.learn.springcore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column(nullable = false)
    @NotBlank(message = "First name is mandatory")
    protected String firstName;
    @Column(nullable = false)
    @NotBlank(message = "Last name is mandatory")
    protected String lastName;
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Username is mandatory")
    protected String username;
    @Column(nullable = false)
    @NotBlank(message = "Password is mandatory")
    protected String password;
    @Column(nullable = false)
    @NotNull(message = "Activation status is mandatory")
    protected Boolean isActive;

}
