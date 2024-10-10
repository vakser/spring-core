package com.epam.learn.springcore;

import com.epam.learn.springcore.controller.TrainingTypeController;
import com.epam.learn.springcore.entity.TrainingType;
import com.epam.learn.springcore.service.TrainingTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.epam.learn.springcore.service.UserService;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingTypeController.class)
public class TrainingTypeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TrainingTypeService trainingTypeService;
    @MockBean
    private UserService userService;

    private List<TrainingType> mockTrainingTypes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockTrainingTypes = Arrays.asList(
                new TrainingType(1, "Yoga"),
                new TrainingType(2, "Fitness")
        );
    }

    @Test
    public void testGetAllTrainingTypes_Success() throws Exception {
        // Mock user authentication success
        when(userService.authenticate(anyString(), anyString())).thenReturn(true);
        when(trainingTypeService.getAllTrainingTypes()).thenReturn(mockTrainingTypes);

        // Prepare headers with authorization
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "John.Doe:password");

        // Perform the GET request
        ResultActions response = mockMvc.perform(get("/api/training-types")
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON));

        // Verify the response
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(mockTrainingTypes.size()))
                .andExpect(jsonPath("$[0].name").value("Yoga"))
                .andExpect(jsonPath("$[1].name").value("Fitness"));
    }

    @Test
    public void testGetAllTrainingTypesUnauthorized() throws Exception {
        // Mock user authentication failure
        when(userService.authenticate(anyString(), anyString())).thenReturn(false);

        // Prepare headers with authorization
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "username:wrongpassword");

        // Perform the GET request
        ResultActions response = mockMvc.perform(get("/api/training-types")
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON));

        // Expect unauthorized response
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void testGetAllTrainingTypesNoAuthHeader() throws Exception {
        // Perform the GET request without the Authorization header
        ResultActions response = mockMvc.perform(get("/api/training-types")
                .contentType(MediaType.APPLICATION_JSON));

        // Expect unauthorized response (or null list)
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }
}
