package com.epam.learn.springcore.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequest {
    private String username;
    private String password;
}
