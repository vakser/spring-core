package com.epam.learn.springcore.service;

import com.epam.learn.springcore.dao.UserRepository;
import com.epam.learn.springcore.entity.User;
import com.epam.learn.springcore.exception.TrainerNotFoundException;
import com.epam.learn.springcore.exception.UserNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User " + username + " not found"));
        return user.getPassword().equals(password);
    }

    // Password generation utility
    public String generateRandomPassword() {
        //return RandomStringUtils.secure().next(10);
        return RandomStringUtils.randomAlphanumeric(10);
    }

    public String calculateUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        int count = 1;
        String finalUsername = baseUsername;
        while (userRepository.findByUsername(finalUsername).isPresent()) {
            finalUsername = baseUsername + count;
            count++;
        }
        return finalUsername;
    }

    public void changePassword(String username, String newPassword) {
        log.info("Changing password for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException("User " + username + " not found"));
        user.setPassword(newPassword);
        userRepository.save(user);
        log.info("Password for user {} changed", username);
    }

}
