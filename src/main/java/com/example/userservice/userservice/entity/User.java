package com.example.userservice.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String phone;

    @Column(nullable = false)
    private String role;

    private boolean isVerified;

    // For both students and instructors → courseIds
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_courses",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "course_id")
    private List<Long> courseIds;
}
