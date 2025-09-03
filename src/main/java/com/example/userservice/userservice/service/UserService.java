package com.example.userservice.userservice.service;

import com.example.userservice.userservice.dto.*;

public interface UserService {
    UserResponse registerUser(UserRegisterRequest request);
    AuthResponse loginUser(UserLoginRequest request);
    UserResponse updateUser(Long id,UserUpdateRequest request);
    UserResponse getUserById(Long id);
    void deleteUser(Long id);
    UserResponse enrollInCourse(Long userId, Long courseId);
    UserResponse addCourseToInstructor(Long instructorId, Long courseId);
}
