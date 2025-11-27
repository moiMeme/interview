package com.interview.controller;

import com.interview.model.Order;
import com.interview.model.User;
import com.interview.repository.OrderRepository;
import com.interview.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// VIOLATION: Controller has business logic (should be thin)
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepository; // VIOLATION: Controller directly accessing repository

    // VIOLATION: Using Map instead of DTO
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> request) {
        // VIOLATION: No input validation
        // VIOLATION: Direct access to map without checking keys
        String name = request.get("name");
        String email = request.get("email");
        String password = request.get("password");
        String role = request.get("role");

        // VIOLATION: Business logic in controller
        if (email == null || !email.contains("@")) {
            return ResponseEntity.badRequest().body("Invalid email");
        }

        String result = userService.processUserRegistration(name, email, password, role);

        // VIOLATION: String comparison for flow control
        if (result.equals("SUCCESS")) {
            return ResponseEntity.ok("User registered successfully");
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        String result = userService.processLogin(email, password);

        Map<String, Object> response = new HashMap<>();
        response.put("message", result);
        response.put("timestamp", LocalDateTime.now());

        // VIOLATION: Different response formats based on string matching
        if (result.equals("Login successful")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }

    // VIOLATION: No pagination for potentially large datasets
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.findUserById(id);

        // VIOLATION: Returning null check instead of 404
        if (user == null) {
            return ResponseEntity.ok(null);
        }

        return ResponseEntity.ok(user);
    }

    // VIOLATION: Payment processing in user controller
    @PostMapping("/{id}/payment")
    public ResponseEntity<String> processPayment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> paymentData) {

        // VIOLATION: Type casting without validation
        double amount = (double) paymentData.get("amount");
        String method = (String) paymentData.get("method");

        boolean success = userService.processPayment(id, amount, method);

        if (success) {
            return ResponseEntity.ok("Payment processed");
        } else {
            return ResponseEntity.badRequest().body("Payment failed");
        }
    }

    // VIOLATION: Report generation in controller
    @GetMapping("/{id}/report")
    public ResponseEntity<String> getUserReport(@PathVariable Long id) {
        String report = userService.generateUserReport(id);
        return ResponseEntity.ok(report);
    }

    // VIOLATION: Exposing internal metrics
    @GetMapping("/metrics/connections")
    public ResponseEntity<Map<String, Integer>> getMetrics() {
        Map<String, Integer> metrics = new HashMap<>();
        metrics.put("activeConnections", userService.getActiveConnections());
        metrics.put("totalUsers", UserService.totalUsers);
        return ResponseEntity.ok(metrics);
    }

    // VIOLATION: Delete without checking dependencies
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            // VIOLATION: Direct repository manipulation in controller
            User user = userService.findUserById(id);
            if (user != null) {
                // VIOLATION: Not checking if user has orders before deleting
                orderRepository.deleteAll(orderRepository.findByUserId(id));
                // Missing: userRepository.delete(user);
                return ResponseEntity.ok("User deleted");
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // VIOLATION: Exposing internal error details
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // VIOLATION: Dangerous endpoint that modifies state
    @PostMapping("/admin/reset")
    public ResponseEntity<String> resetSystem() {
        UserService.totalUsers = 0;
        userService.cleanup();
        return ResponseEntity.ok("System reset");
    }
}
