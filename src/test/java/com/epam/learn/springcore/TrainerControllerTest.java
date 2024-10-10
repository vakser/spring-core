package com.epam.learn.springcore;

import com.epam.learn.springcore.controller.TrainerController;
import com.epam.learn.springcore.dto.*;
import com.epam.learn.springcore.service.TrainerService;
import com.epam.learn.springcore.service.UserService;
import com.epam.learn.springcore.specification.TrainerTrainingSearchCriteria;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainerController.class)
public class TrainerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TrainerService trainerService;
    @MockBean
    private UserService userService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testRegisterTrainer() throws Exception {
        TrainerRegistrationRequest request = TrainerRegistrationRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .specializationId(2)
                .build();
        UserResponse response = new UserResponse("John.Doe", "registered");

        when(trainerService.createTrainer(any(TrainerRegistrationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("John.Doe"));
    }

    @Test
    void testGetTrainerProfile() throws Exception {
        String username = "John.Doe";
        GetTrainerProfileResponse profileResponse = GetTrainerProfileResponse.builder()
                .firstName("John")
                .lastName("Doe")
                .specializationId(2)
                .isActive(true)
                .build();

        String token = "John.Doe:password";
        when(userService.authenticate(anyString(), anyString())).thenReturn(true);
        when(trainerService.selectTrainer(username)).thenReturn(profileResponse);

        mockMvc.perform(get("/api/trainers/{username}", username)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.specializationId").value(2))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void testUpdateTrainerProfile() throws Exception {
        String username = "John.Doe";
        TrainerUpdateRequest updateRequest = TrainerUpdateRequest.builder()
                .username("John.Doe")
                .firstName("Mary")
                .lastName("Public")
                .isActive(true)
                .build();
        TrainerUpdateResponse updateResponse = TrainerUpdateResponse.builder()
                .username("John.Doe")
                .firstName("Mary")
                .lastName("Public")
                .isActive(true)
                .build();

        String token = "John.Doe:password";
        when(userService.authenticate(anyString(), anyString())).thenReturn(true);
        when(trainerService.updateTrainer(any(TrainerUpdateRequest.class))).thenReturn(updateResponse);

        mockMvc.perform(put("/api/trainers/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.firstName").value("Mary"))
                .andExpect(jsonPath("$.lastName").value("Public"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void testChangeActivationStatus() throws Exception {
        String username = "John.Doe";
        ActivationRequest activationRequest = ActivationRequest.builder()
                .username("John.Doe")
                .isActive(false)
                .build();

        String token = "John.Doe:password";
        when(userService.authenticate(anyString(), anyString())).thenReturn(true);
        when(trainerService.selectTrainer(username)).thenReturn(GetTrainerProfileResponse.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .isActive(false)
                .build());

        mockMvc.perform(patch("/api/trainers/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .content(objectMapper.writeValueAsString(activationRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTrainerTrainings() throws Exception {
        String username = "John.Doe";
        TrainerTrainingSearchCriteria criteria = new TrainerTrainingSearchCriteria(username, null, null, null);
        List<TrainerTrainingResponse> trainings = new ArrayList<>();

        String token = "John.Doe:password";
        when(userService.authenticate(anyString(), anyString())).thenReturn(true);
        when(trainerService.getTrainerTrainings(criteria)).thenReturn(trainings);

        mockMvc.perform(get("/api/trainers/{username}/trainings", username)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(trainings.size()));
    }

    @Test
    void testAddTraining() throws Exception {
        String username = "John.Doe";
        AddTrainingRequest addTrainingRequest = AddTrainingRequest.builder()
                .traineeUsername("Mary.Public")
                .trainerUsername("John.Doe")
                .trainingName("Hatha Yoga")
                .trainingDate(LocalDate.of(2024, 10, 10))
                .trainingDuration(60)
                .build();

        String token = "John.Doe:password";
        when(userService.authenticate(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/api/trainers/{username}/trainings", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .content(objectMapper.writeValueAsString(addTrainingRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTrainerProfileUnauthorized() throws Exception {
        String username = "Ben.Wallace";

        mockMvc.perform(get("/api/trainers/{username}", username))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetTrainerProfileBadRequest() throws Exception {
        String username = "John.Doe";
        String token = "Ben.Wallace:password";

        mockMvc.perform(get("/api/trainers/{username}", username)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isBadRequest());
    }


}
