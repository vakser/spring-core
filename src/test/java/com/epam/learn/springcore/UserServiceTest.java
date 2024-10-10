package com.epam.learn.springcore;

import com.epam.learn.springcore.dao.UserRepository;
import com.epam.learn.springcore.entity.User;
import com.epam.learn.springcore.exception.UserNotFoundException;
import com.epam.learn.springcore.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1, "John", "Doe", "John.Doe", "password123", true);
    }

    @Test
    void testAuthenticateSuccess() {
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));
        boolean isAuthenticated = userService.authenticate("John.Doe", "password123");
        assertTrue(isAuthenticated);
        verify(userRepository, times(1)).findByUsername("John.Doe");
    }

    @Test
    void testAuthenticateIncorrectPassword() {
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));
        boolean isAuthenticated = userService.authenticate("John.Doe", "wrongPassword");
        assertFalse(isAuthenticated);
        verify(userRepository, times(1)).findByUsername("John.Doe");
    }

    @Test
    void testAuthenticateUserNotFound() {
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.empty());
        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.authenticate("John.Doe", "password123"));
        assertEquals("User John.Doe not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("John.Doe");
    }

    @Test
    void testGenerateRandomPassword() {
        String password = userService.generateRandomPassword();
        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @Test
    void testCalculateUsernameUnique() {
        when(userRepository.findByUsername("Unique.User")).thenReturn(Optional.empty());
        String username = userService.calculateUsername("Unique", "User");
        assertEquals("Unique.User", username);
    }

    @Test
    void testCalculateUsernameWithConflict() {
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(new User()));
        when(userRepository.findByUsername("John.Doe1")).thenReturn(Optional.empty());
        String username = userService.calculateUsername("John", "Doe");
        assertEquals("John.Doe1", username);
    }

    @Test
    void testChangePassword() {
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));
        userService.changePassword("John.Doe", "newPassword");
        verify(userRepository, times(1)).save(user);
        assertEquals("newPassword", user.getPassword());
    }
}
