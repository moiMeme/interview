package com.interview.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String productName;
    private double amount;
    private String status; // Using String instead of Enum
    private LocalDateTime orderDate;
    private String paymentMethod;
}
