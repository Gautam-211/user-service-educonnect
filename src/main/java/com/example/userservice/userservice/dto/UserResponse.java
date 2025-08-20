package com.example.userservice.userservice.dto;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse implements Serializable {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private boolean isVerified;
}
