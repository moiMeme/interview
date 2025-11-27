package com.interview.service;

import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// VIOLATION: Poor error handling throughout
@Service
public class PaymentProcessor {

    // VIOLATION: Hardcoded credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/payments";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "password123";

    // VIOLATION: Exception swallowing
    public boolean processPayment(String cardNumber, double amount) {
        try {
            validateCard(cardNumber);
            chargeCard(cardNumber, amount);
            return true;
        } catch (Exception e) {
            // VIOLATION: Catching generic Exception
            // VIOLATION: Only printing, not re-throwing or handling properly
            e.printStackTrace();
            return false;
        }
    }

    // VIOLATION: Throws generic Exception instead of specific exception
    private void validateCard(String cardNumber) throws Exception {
        if (cardNumber == null) {
            throw new Exception("Card number is null"); // VIOLATION: Generic exception
        }

        // VIOLATION: No actual validation logic
        if (cardNumber.length() != 16) {
            throw new RuntimeException("Invalid card"); // VIOLATION: RuntimeException for business logic
        }
    }

    // VIOLATION: Resource leak - Connection not closed
    private void chargeCard(String cardNumber, double amount) throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        // VIOLATION: SQL injection vulnerability
        Statement stmt = conn.createStatement();
        String sql = "INSERT INTO charges VALUES ('" + cardNumber + "', " + amount + ")";
        stmt.executeUpdate(sql);
        // VIOLATION: Resources never closed - leak
    }

    // VIOLATION: Multiple error handling issues
    public void recordTransaction(String transactionId, String details) {
        FileWriter writer = null;
        try {
            writer = new FileWriter("/var/log/transactions.log", true);
            writer.write(transactionId + "," + details + "\n");
        } catch (IOException e) {
            // VIOLATION: Empty catch block - error silently ignored
        }
        // VIOLATION: Resource not closed in finally block
    }

    // VIOLATION: Mixing checked and unchecked exceptions poorly
    public double calculateFee(double amount) {
        try {
            if (amount < 0) {
                throw new IllegalArgumentException("Negative amount");
            }

            if (amount == 0) {
                throw new Exception("Zero amount"); // VIOLATION: Checked exception for validation
            }

            return amount * 0.03;
        } catch (IllegalArgumentException e) {
            return 0; // VIOLATION: Returning magic number on error
        } catch (Exception e) {
            throw new RuntimeException(e); // VIOLATION: Converting checked to unchecked
        }
    }

    // VIOLATION: Error codes instead of exceptions
    public int refundPayment(String transactionId) {
        if (transactionId == null) {
            return -1; // VIOLATION: Magic number error code
        }

        if (transactionId.isEmpty()) {
            return -2; // VIOLATION: Another magic number
        }

        try {
            processRefund(transactionId);
            return 0; // VIOLATION: Magic number for success
        } catch (Exception e) {
            return -99; // VIOLATION: Generic error code
        }
    }

    private void processRefund(String transactionId) throws Exception {
        // VIOLATION: Throwing Exception in method signature
        throw new Exception("Refund failed");
    }

    // VIOLATION: Method declares exception but doesn't throw it
    public void voidTransaction(String transactionId) throws IOException {
        System.out.println("Voiding: " + transactionId);
        // No IOException actually thrown
    }

    // VIOLATION: Catching Throwable
    public void dangerousMethod() {
        try {
            // Some operation
            int result = 10 / 0;
        } catch (Throwable t) {
            // VIOLATION: Catching Throwable catches Errors too
            System.out.println("Something went wrong");
        }
    }

    // VIOLATION: Exception for flow control
    public boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            // VIOLATION: Using exceptions for normal flow control
            return false;
        }
    }

    // VIOLATION: Creating exception but not throwing it
    public void validateAmount(double amount) {
        if (amount < 0) {
            new IllegalArgumentException("Invalid amount"); // VIOLATION: Created but not thrown
        }
    }

    // VIOLATION: Losing original exception context
    public void complexOperation() {
        try {
            riskyOperation();
        } catch (Exception e) {
            // VIOLATION: Throwing new exception without cause
            throw new RuntimeException("Operation failed");
        }
    }

    private void riskyOperation() throws Exception {
        throw new Exception("Original error with important context");
    }

    // VIOLATION: Not closing AutoCloseable resource
    public String readConfig() {
        try {
            FileWriter writer = new FileWriter("config.txt");
            writer.write("config");
            // VIOLATION: Not closing AutoCloseable resource
            return "success";
        } catch (IOException e) {
            return null; // VIOLATION: Returning null on error
        }
    }
}
