package com.epam.learn.springcore.service;

import com.epam.learn.springcore.dao.UserRepository;
import com.epam.learn.springcore.entity.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    boolean authenticate(String username, String password) {
        User user = userRepository.findByUsername(username);
        return user != null && user.getPassword().equals(password);
    }

    // Password generation utility
    String generateRandomPassword() {
        //return RandomStringUtils.secure().next(10);
        return RandomStringUtils.randomAlphanumeric(10);
    }

    protected String calculateUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        int count = 1;
        String finalUsername = baseUsername;
        while (userRepository.findByUsername(finalUsername) != null) {
            finalUsername = baseUsername + count;
            count++;
        }
        return finalUsername;
    }
}
