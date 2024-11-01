package com.epam.learn.springcore.exception;

import com.epam.learn.springcore.dto.CustomErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TrainerNotFoundException.class)
    public ResponseEntity<Object> handleTrainerNotFoundException(TrainerNotFoundException ex) {
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(HttpStatus.NOT_FOUND, "Trainer doesn't exist", ex.getMessage());
        return new ResponseEntity<>(customErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TraineeNotFoundException.class)
    public ResponseEntity<Object> handleTraineeNotFoundException(TraineeNotFoundException ex) {
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(HttpStatus.NOT_FOUND, "Trainee doesn't exist", ex.getMessage());
        return new ResponseEntity<>(customErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TrainingTypeNotFoundException.class)
    public ResponseEntity<Object> handleTrainingTypeNotFoundException(TrainingTypeNotFoundException ex) {
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(HttpStatus.NOT_FOUND, "Training type doesn't exist", ex.getMessage());
        return new ResponseEntity<>(customErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(HttpStatus.UNAUTHORIZED, "Bad credentials", ex.getMessage());
        return new ResponseEntity<>(customErrorResponse, HttpStatus.UNAUTHORIZED);
    }
}
