package com.epam.learn.springcore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingType {
    @Id
    private Integer id;
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Name is mandatory")
    private String name;

}
