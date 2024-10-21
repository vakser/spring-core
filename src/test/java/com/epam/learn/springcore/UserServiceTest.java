package com.epam.learn.springcore;

import com.epam.learn.springcore.dao.UserRepository;
import com.epam.learn.springcore.entity.User;
import com.epam.learn.springcore.exception.TrainerNotFoundException;
import com.epam.learn.springcore.exception.UserNotFoundException;
import com.epam.learn.springcore.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAuthenticateSuccess() {
        // Given
        String username = "johndoe";
        String password = "password123";
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        // Mock repository behavior
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        boolean result = userService.authenticate(username, password);

        // Then
        assertTrue(result);
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void testAuthenticateFailure_WrongPassword() {
        // Given
        String username = "johndoe";
        String password = "wrongPassword";
        User user = new User();
        user.setUsername(username);
        user.setPassword("correctPassword");

        // Mock repository behavior
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        boolean result = userService.authenticate(username, password);

        // Then
        assertFalse(result);
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void testAuthenticateFailure_UserNotFound() {
        // Given
        String username = "unknownUser";
        String password = "password123";

        // Mock repository behavior to throw exception
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(UserNotFoundException.class, () -> userService.authenticate(username, password));
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void testGenerateRandomPassword() {
        // When
        String randomPassword = userService.generateRandomPassword();

        // Then
        assertNotNull(randomPassword);
        assertEquals(10, randomPassword.length());
        assertTrue(randomPassword.matches("[A-Za-z0-9]+"));  // Check that it's alphanumeric
    }

    @Test
    public void testCalculateUsername_FirstAttempt() {
        // Given
        String firstName = "John";
        String lastName = "Doe";
        String expectedUsername = "John.Doe";

        // Mock repository behavior to simulate username is not present
        when(userRepository.findByUsername(expectedUsername)).thenReturn(Optional.empty());

        // When
        String calculatedUsername = userService.calculateUsername(firstName, lastName);

        // Then
        assertEquals(expectedUsername, calculatedUsername);
        verify(userRepository, times(1)).findByUsername(expectedUsername);
    }

    @Test
    public void testCalculateUsername_SecondAttempt() {
        // Given
        String firstName = "John";
        String lastName = "Doe";
        String baseUsername = "John.Doe";
        String expectedUsername = "John.Doe1";

        // Mock repository behavior to simulate that the first username is taken
        when(userRepository.findByUsername(baseUsername)).thenReturn(Optional.of(new User()));
        when(userRepository.findByUsername(expectedUsername)).thenReturn(Optional.empty());

        // When
        String calculatedUsername = userService.calculateUsername(firstName, lastName);

        // Then
        assertEquals(expectedUsername, calculatedUsername);
        verify(userRepository, times(1)).findByUsername(baseUsername);
        verify(userRepository, times(1)).findByUsername(expectedUsername);
    }

    @Test
    public void testChangePassword() {
        // Given
        String username = "johndoe";
        String newPassword = "newPassword123";
        User user = new User();
        user.setUsername(username);
        user.setPassword("oldPassword");

        // Mock repository behavior
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        userService.changePassword(username, newPassword);

        // Then
        assertEquals(newPassword, user.getPassword());
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testChangePassword_UserNotFound() {
        // Given
        String username = "unknownUser";
        String newPassword = "newPassword123";

        // Mock repository behavior to simulate user not found
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(TrainerNotFoundException.class, () -> userService.changePassword(username, newPassword));
        verify(userRepository, times(1)).findByUsername(username);
    }
}
