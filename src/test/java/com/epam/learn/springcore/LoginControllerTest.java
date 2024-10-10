package com.epam.learn.springcore;

import com.epam.learn.springcore.controller.LoginController;
import com.epam.learn.springcore.dto.ChangePasswordRequest;
import com.epam.learn.springcore.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
public class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for Login Endpoint
    @Test
    public void testLogin_Success() throws Exception {
        // Mocking user authentication success
        when(userService.authenticate("username", "password")).thenReturn(true);

        // Prepare headers with authorization
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "username:password");

        // Perform the GET request
        ResultActions response = mockMvc.perform(get("/api/login")
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON));

        // Verify the response status is 200 OK
        response.andExpect(status().isOk());
    }

    @Test
    public void testLogin_Unauthorized() throws Exception {
        // Mocking user authentication failure
        when(userService.authenticate("username", "wrongpassword")).thenReturn(false);

        // Prepare headers with wrong credentials
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "username:wrongpassword");

        // Perform the GET request
        ResultActions response = mockMvc.perform(get("/api/login")
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON));

        // Verify the response status is 401 Unauthorized
        response.andExpect(status().isUnauthorized());
    }

    @Test
    public void testLogin_NoAuthHeader() throws Exception {
        // Perform the GET request without Authorization header
        ResultActions response = mockMvc.perform(get("/api/login")
                .contentType(MediaType.APPLICATION_JSON));

        // Verify the response status is 401 Unauthorized
        response.andExpect(status().isUnauthorized());
    }

    // Test for Change Password Endpoint
    @Test
    public void testChangePassword_Success() throws Exception {
        // Mocking successful password change
        when(userService.authenticate("username", "oldPassword")).thenReturn(true);

        ChangePasswordRequest request = new ChangePasswordRequest("username", "oldPassword", "newPassword");

        // Perform the PUT request
        ResultActions response = mockMvc.perform(put("/api/login/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Verify the response status is 200 OK
        response.andExpect(status().isOk());

        // Verify that the service's changePassword method was called
        verify(userService).changePassword("username", "newPassword");
    }

    @Test
    public void testChangePassword_Unauthorized() throws Exception {
        // Mocking authentication failure
        when(userService.authenticate("username", "wrongOldPassword")).thenReturn(false);

        ChangePasswordRequest request = new ChangePasswordRequest("username", "wrongOldPassword", "newPassword");

        // Perform the PUT request
        ResultActions response = mockMvc.perform(put("/api/login/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Verify the response status is 401 Unauthorized
        response.andExpect(status().isUnauthorized());
    }
}
