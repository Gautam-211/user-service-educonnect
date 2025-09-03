package com.example.userservice.userservice.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private boolean isVerified;
    // List of purchased courses (for students)
    // List of created courses (for instructors)
    private List<Long> courseIds;
}
