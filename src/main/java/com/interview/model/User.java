package com.interview.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password; // Storing plain text password - SECURITY ISSUE
    private String role;
    private boolean active;
    private double accountBalance;
    private int loginAttempts;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}
