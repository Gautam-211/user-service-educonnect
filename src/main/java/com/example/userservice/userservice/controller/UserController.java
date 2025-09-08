package com.example.userservice.userservice.controller;

import com.example.userservice.userservice.dto.*;
import com.example.userservice.userservice.security.JwtService;
import com.example.userservice.userservice.service.UserService;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(userService.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(userService.loginUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest request
    ) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<UserResponse> enrollInCourse(
            @PathVariable Long courseId,
            @RequestHeader("Authorization") String authHeader
    ) {
        // Extract token (remove "Bearer " prefix)
        String token = authHeader.substring(7);
        Long userId = Long.parseLong(jwtService.extractUserId(token));

        return ResponseEntity.ok(userService.enrollInCourse(userId, courseId));
    }

    @PostMapping("/{instructorId}/courses/{courseId}")
    public ResponseEntity<UserResponse> addCourseToInstructor(
            @PathVariable Long instructorId,
            @PathVariable Long courseId
    ) {
        return ResponseEntity.ok(userService.addCourseToInstructor(instructorId, courseId));
    }
}
