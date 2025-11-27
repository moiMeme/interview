package com.interview.service;

import com.interview.model.Order;
import com.interview.model.User;
import com.interview.repository.OrderRepository;
import com.interview.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

// VIOLATION: This class has too many responsibilities (SRP violation)
// It handles user management, email, logging, payment, validation, etc.
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    // VIOLATION: Public mutable static field - thread safety issue
    public static int totalUsers = 0;

    // VIOLATION: Shared mutable state without synchronization
    private int activeConnections = 0;

    // VIOLATION: Giant method doing too many things
    // VIOLATION: Poor error handling
    // VIOLATION: Magic numbers
    // VIOLATION: No input validation
    public String processUserRegistration(String name, String email, String password, String role) {
        // VIOLATION: No null checks
        // VIOLATION: Printing to console instead of logging
        System.out.println("Processing registration for: " + email);

        try {
            // VIOLATION: Business logic mixed with data access
            User existingUser = userRepository.findByEmail(email);

            if (existingUser != null) {
                // VIOLATION: Returning error messages as strings instead of exceptions
                return "ERROR: User already exists";
            }

            // VIOLATION: No password validation or hashing
            if (password.length() < 3) {
                return "ERROR: Password too short";
            }

            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password); // VIOLATION: Storing plain text password
            user.setRole(role);
            user.setActive(true);
            user.setAccountBalance(0.0);
            user.setLoginAttempts(0);
            user.setCreatedAt(LocalDateTime.now());

            userRepository.save(user);

            // VIOLATION: Thread safety issue - race condition
            totalUsers++;
            activeConnections++;

            // VIOLATION: Email sending logic in service (SRP violation)
            sendWelcomeEmail(user);

            // VIOLATION: Logging logic in service
            logUserActivity(user.getId(), "REGISTRATION");

            // VIOLATION: Hard-coded business logic
            if (role.equals("PREMIUM")) {
                user.setAccountBalance(100.0);
                userRepository.save(user);
            }

            // VIOLATION: Magic number
            if (totalUsers % 100 == 0) {
                System.out.println("Milestone reached: " + totalUsers + " users!");
            }

            return "SUCCESS";

        } catch (Exception e) {
            // VIOLATION: Swallowing exception details
            System.out.println("Error occurred");
            return "ERROR";
        }
    }

    // VIOLATION: No separation between email service
    private void sendWelcomeEmail(User user) {
        // VIOLATION: Simulating email with Thread.sleep in production code
        try {
            Thread.sleep(100);
            System.out.println("Email sent to: " + user.getEmail());
        } catch (InterruptedException e) {
            // VIOLATION: Empty catch block
        }
    }

    // VIOLATION: Logging should be in separate service
    private void logUserActivity(Long userId, String activity) {
        System.out.println("User " + userId + " performed: " + activity);
    }

    // VIOLATION: God method doing everything
    public String processLogin(String email, String password) {
        try {
            User user = userRepository.findByEmail(email);

            // VIOLATION: Using null instead of Optional
            if (user == null) {
                return "User not found";
            }

            // VIOLATION: Plain text password comparison
            if (!user.getPassword().equals(password)) {
                user.setLoginAttempts(user.getLoginAttempts() + 1);

                // VIOLATION: Magic number
                if (user.getLoginAttempts() >= 5) {
                    user.setActive(false);
                    userRepository.save(user);
                    return "Account locked due to too many attempts";
                }

                userRepository.save(user);
                return "Invalid password";
            }

            if (!user.isActive()) {
                return "Account is inactive";
            }

            user.setLastLogin(LocalDateTime.now());
            user.setLoginAttempts(0);
            userRepository.save(user);

            // VIOLATION: Thread safety issue
            activeConnections++;

            logUserActivity(user.getId(), "LOGIN");

            return "Login successful";

        } catch (Exception e) {
            e.printStackTrace();
            return "Login failed";
        }
    }

    // VIOLATION: Payment processing in UserService (SRP violation)
    // VIOLATION: Poor error handling
    // VIOLATION: Thread safety issues
    public boolean processPayment(Long userId, double amount, String paymentMethod) {
        User user = userRepository.findByEmail(findUserById(userId).getEmail());

        // VIOLATION: Complex nested conditions
        if (user != null) {
            if (user.isActive()) {
                if (amount > 0) {
                    if (paymentMethod.equals("CREDIT_CARD") || paymentMethod.equals("DEBIT_CARD")) {
                        // VIOLATION: Random to simulate payment - unpredictable behavior
                        Random random = new Random();
                        boolean success = random.nextBoolean();

                        if (success) {
                            // VIOLATION: Race condition - not atomic
                            double currentBalance = user.getAccountBalance();
                            user.setAccountBalance(currentBalance + amount);
                            userRepository.save(user);

                            // VIOLATION: Creating order in payment method
                            Order order = new Order();
                            order.setUserId(userId);
                            order.setProductName("Product");
                            order.setAmount(amount);
                            order.setStatus("COMPLETED");
                            order.setOrderDate(LocalDateTime.now());
                            order.setPaymentMethod(paymentMethod);
                            orderRepository.save(order);

                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // VIOLATION: Duplicate code
    public User findUserById(Long id) {
        try {
            return userRepository.findById(id).get(); // VIOLATION: Using get() without checking
        } catch (Exception e) {
            return null;
        }
    }

    // VIOLATION: Exposing all users without pagination
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // VIOLATION: Business logic for reports in UserService
    public String generateUserReport(Long userId) {
        User user = findUserById(userId);
        List<Order> orders = orderRepository.findByUserId(userId);

        // VIOLATION: String concatenation in loop
        String report = "User Report\n";
        report += "Name: " + user.getName() + "\n";
        report += "Email: " + user.getEmail() + "\n";
        report += "Balance: " + user.getAccountBalance() + "\n";
        report += "Orders:\n";

        for (Order order : orders) {
            report += "- " + order.getProductName() + ": $" + order.getAmount() + "\n";
        }

        return report;
    }

    // VIOLATION: Exposing internal state
    public int getActiveConnections() {
        return activeConnections;
    }

    // VIOLATION: Method with side effects not clear from name
    public void cleanup() {
        activeConnections = 0;
    }
}
