package com.example.userservice.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginRequest {
    @Email(message = "should be a valid email")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
