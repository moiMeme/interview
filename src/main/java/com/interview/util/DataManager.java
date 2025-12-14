package com.interview.util;

import com.interview.model.Order;
import com.interview.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// VIOLATION: class that does everything
// VIOLATION: Mixing JPA and JDBC
// VIOLATION: Business logic in utility class
@Component
public class DataManager {

    @PersistenceContext
    private EntityManager entityManager;

    // VIOLATION: Hardcoded database credentials
    private static final String URL = "jdbc:h2:mem:testdb";
    private static final String USER = "sa";
    private static final String PASS = "";

    // VIOLATION: SQL in utility class (should be in repository)
    // VIOLATION: Resource leaks
    public List<User> findAllUsersSQL() throws SQLException {
        List<User> users = new ArrayList<>();
        Connection conn = DriverManager.getConnection(URL, USER, PASS);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM users");

        while (rs.next()) {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            users.add(user);
        }

        // VIOLATION: Resources not closed - memory leak
        return users;
    }

    // VIOLATION: SQL injection vulnerability
    public User findUserByEmailUnsafe(String email) throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASS);
        Statement stmt = conn.createStatement();
        // VIOLATION: String concatenation in SQL - SQL injection
        String query = "SELECT * FROM users WHERE email = '" + email + "'";
        ResultSet rs = stmt.executeQuery(query);

        if (rs.next()) {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            return user;
        }

        return null;
    }

    // VIOLATION: Mixing JPA with JDBC in same class
    public void saveUserJPA(User user) {
        entityManager.persist(user);
    }

    // VIOLATION: Business logic in utility class
    public double calculateOrderTotal(Long userId) throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASS);
        PreparedStatement pstmt = conn.prepareStatement(
                "SELECT SUM(amount) FROM orders WHERE user_id = ?"
        );
        pstmt.setLong(1, userId);
        ResultSet rs = pstmt.executeQuery();

        double total = 0;
        if (rs.next()) {
            total = rs.getDouble(1);
        }

        // VIOLATION: Not closing resources
        return total;
    }

    // VIOLATION: Utility method modifying state
    public void updateAllUserBalances() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASS);
        Statement stmt = conn.createStatement();
        // VIOLATION: Dangerous update without WHERE clause
        stmt.executeUpdate("UPDATE users SET account_balance = account_balance * 1.05");
        // Resources not closed
    }

    // VIOLATION: Complex data transformation in utility
    public String formatUserData(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append(user.getId()).append("|");
        sb.append(user.getName()).append("|");
        sb.append(user.getEmail()).append("|");
        sb.append(user.getPassword()).append("|"); // VIOLATION: Exposing password
        return sb.toString();
    }

    // VIOLATION: Static method accessing database
    public static int countUsers() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            // VIOLATION: Swallowing exception in static method
            e.printStackTrace();
        }
        return -1; // VIOLATION: Magic number for error
    }

    // VIOLATION: Validation logic in utility class
    public boolean validateUser(User user) {
        if (user == null) return false;
        if (user.getName() == null || user.getName().length() < 2) return false;
        if (user.getEmail() == null || !user.getEmail().contains("@")) return false;
        if (user.getPassword() == null || user.getPassword().length() < 3) return false;
        return true;
    }

    // VIOLATION: Batch operation without transaction management
    public void bulkInsertOrders(List<Order> orders) throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASS);
        for (Order order : orders) {
            // VIOLATION: N+1 query problem - should use batch insert
            PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO orders (user_id, product_name, amount, status) VALUES (?, ?, ?, ?)"
            );
            pstmt.setLong(1, order.getUserId());
            pstmt.setString(2, order.getProductName());
            pstmt.setDouble(3, order.getAmount());
            pstmt.setString(4, order.getStatus());
            pstmt.executeUpdate();
            // Not closing PreparedStatement in loop
        }
        // Not closing connection
    }
}
