package com.example.userservice.userservice.service;

import com.example.userservice.userservice.client.CourseClient;
import com.example.userservice.userservice.dto.*;
import com.example.userservice.userservice.entity.User;
import com.example.userservice.userservice.exception.*;
import com.example.userservice.userservice.repository.UserRepository;
import com.example.userservice.userservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CourseClient courseClient;

    @Override
    public UserResponse registerUser(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("A user with email '" + request.getEmail() + "' already exists.");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole())
                .isVerified(false)
                .courseIds(new ArrayList<>())
                .build();

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    @Override
    public AuthResponse loginUser(UserLoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with email: " + request.getEmail());
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Password is incorrect.");
        }

        Map<String, Object> extraClaims = new HashMap<>();

        extraClaims.put("role", user.getRole());
        extraClaims.put("userId", String.valueOf(user.getId()));

        String jwtToken = jwtService.generateToken(extraClaims, user.getEmail());

        return new AuthResponse(jwtToken, "Login Successful");
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found."));

        user.setFullName(request.getFullName());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            user.setPhone(request.getPhone());
        }

        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    @Override
    @Cacheable(value = "users", key = "#id")
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        System.out.println("************************************");
        System.out.println("Fetching user from data with id : " + id);
        System.out.println("************************************");

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found."));
        return mapToResponse(user);
    }

    //
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User with id " + id + " not found.");
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserResponse enrollInCourse(Long userId, Long courseId) {
        // Check if course exists
        if(!courseClient.courseExists(courseId)){
            throw new ResourceNotFoundException("Course with id " + courseId + " not found.");
        }

        // Get the logged-in user
        User student = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Ensure role is STUDENT
        if (!student.getRole().equals("STUDENT")) {
            throw new RoleAccessDeniedException("Only students can enroll in courses");
        }

        // Prevent duplicate enrollment
        if (student.getCourseIds().contains(courseId)) {
            throw new AlreadyExistsException("Already enrolled in this course");
        }

        student.getCourseIds().add(courseId);
        return mapToResponse(userRepository.save(student));
    }

    @Override
    @Transactional
    public UserResponse addCourseToInstructor(Long instructorId, Long courseId) {
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new UserNotFoundException("Instructor not found with id " + instructorId));

        if (!"INSTRUCTOR".equalsIgnoreCase(instructor.getRole())) {
            throw new IllegalArgumentException("User with id " + instructorId + " is not an instructor");
        }

        // Assuming instructor has a List<Long> or Set<Long> for courses
        instructor.getCourseIds().add(courseId);
        User updated = userRepository.save(instructor);

        return mapToResponse(updated);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .isVerified(user.isVerified())
                .courseIds(new ArrayList<>(user.getCourseIds()))
                .build();
    }
}
