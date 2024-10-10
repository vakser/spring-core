package com.epam.learn.springcore;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.epam.learn.springcore.controller.TraineeController;
import com.epam.learn.springcore.dto.*;
import com.epam.learn.springcore.service.TraineeService;
import com.epam.learn.springcore.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;

@WebMvcTest(TraineeController.class)
public class TraineeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TraineeService traineeService;
    @MockBean
    private UserService userService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testRegisterTrainee() throws Exception {
        TraineeRegistrationRequest request = TraineeRegistrationRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .build();
        UserResponse response = new UserResponse("John.Doe", "registered");

        when(traineeService.createTrainee(any(TraineeRegistrationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("John.Doe"));
    }

    @Test
    void testGetTraineeProfile() throws Exception {
        String username = "John.Doe";
        GetTraineeProfileResponse profileResponse = GetTraineeProfileResponse.builder()
                .firstName("John")
                .lastName("Doe")
                .build();

        String token = "John.Doe:password";
        when(userService.authenticate(anyString(), anyString())).thenReturn(true);
        when(traineeService.selectTrainee(username)).thenReturn(profileResponse);

        mockMvc.perform(get("/api/trainees/{username}", username)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void testUpdateTraineeProfile() throws Exception {
        String username = "Mike.Tyson";
        TraineeUpdateRequest updateRequest = TraineeUpdateRequest.builder()
                .username("Mike.Tyson")
                .firstName("Mike")
                .lastName("Tyson")
                .dateOfBirth(LocalDate.of(1966, 6, 30))
                .isActive(true)
                .build();

        TraineeUpdateResponse updateResponse = TraineeUpdateResponse.builder()
                .username("Mike.Tyson")
                .firstName("Muhammad")
                .lastName("Ali")
                .dateOfBirth(LocalDate.of(1942, 1, 17))
                .isActive(true)
                .build();

        String token = "Mike.Tyson:password";
        when(userService.authenticate(anyString(), anyString())).thenReturn(true);
        when(traineeService.updateTrainee(any(TraineeUpdateRequest.class))).thenReturn(updateResponse);

        mockMvc.perform(put("/api/trainees/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.firstName").value("Muhammad"))
                .andExpect(jsonPath("$.lastName").value("Ali"))
                .andExpect(jsonPath("$.dateOfBirth").value(LocalDate.of(1942, 1, 17).toString()));
    }

    @Test
    void testDeleteTraineeProfile() throws Exception {
        String username = "Mike.Tyson";
        String token = "Mike.Tyson:password";

        when(userService.authenticate(anyString(), anyString())).thenReturn(true);
        doNothing().when(traineeService).deleteTrainee(username);

        mockMvc.perform(delete("/api/trainees/{username}", username)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetTraineeProfileUnauthorized() throws Exception {
        String username = "Mike.Tyson";

        mockMvc.perform(get("/api/trainees/{username}", username))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetTraineeProfileBadRequest() throws Exception {
        String username = "Mike.Tyson";
        String token = "differentUsername:password";

        mockMvc.perform(get("/api/trainees/{username}", username)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isBadRequest());
    }



}
