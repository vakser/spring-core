package com.epam.learn.springcore.exception;

public class TrainingTypeNotFoundException extends RuntimeException {
    public TrainingTypeNotFoundException(String message) {
        super(message);
    }
}
