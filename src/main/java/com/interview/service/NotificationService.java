package com.interview.service;

import com.interview.model.User;
import com.interview.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

// VIOLATION: Class with poor structure and naming
@Service
public class NotificationService {

    @Autowired
    private UserRepository userRepository;

    // VIOLATION: Flag parameter
    public void sendNotification(User user, String message, boolean isEmail, boolean isSMS, boolean isPush) {
        // VIOLATION: Complex conditional logic based on flags
        if (isEmail) {
            sendEmail(user, message);
        }
        if (isSMS) {
            sendSMS(user, message);
        }
        if (isPush) {
            sendPush(user, message);
        }
    }

    // VIOLATION: Long parameter list
    public void send(String to, String from, String subject, String body, String type, int priority, boolean urgent, Date scheduleTime) {
        // VIOLATION: Method too long with too many responsibilities
        System.out.println("Sending notification");

        // VIOLATION: Magic numbers
        if (priority > 5) {
            System.out.println("High priority");
        }

        if (urgent) {
            // Send immediately
            if (type.equals("email")) {
                // VIOLATION: Duplicate code
                System.out.println("Sending email from " + from + " to " + to);
                System.out.println("Subject: " + subject);
                System.out.println("Body: " + body);
            } else if (type.equals("sms")) {
                // VIOLATION: Duplicate code
                System.out.println("Sending SMS from " + from + " to " + to);
                System.out.println("Body: " + body);
            }
        } else {
            // Schedule for later
            System.out.println("Scheduling for: " + scheduleTime);
        }
    }

    // VIOLATION: Method name doesn't describe what it does
    public void doStuff(Long userId) {
        User u = userRepository.findById(userId).orElse(null);
        if (u != null) {
            // VIOLATION: Magic strings
            if (u.getRole().equals("ADMIN")) {
                System.out.println("Admin notification");
            } else if (u.getRole().equals("USER")) {
                System.out.println("User notification");
            } else if (u.getRole().equals("PREMIUM")) {
                System.out.println("Premium notification");
            }
        }
    }

    // VIOLATION: Duplicate code from UserService
    private void sendEmail(User user, String message) {
        try {
            Thread.sleep(100);
            System.out.println("Email sent to: " + user.getEmail() + " - " + message);
        } catch (InterruptedException e) {
            // Empty catch
        }
    }

    // VIOLATION: Duplicate code pattern
    private void sendSMS(User user, String message) {
        try {
            Thread.sleep(100);
            System.out.println("SMS sent to: " + user.getEmail() + " - " + message);
        } catch (InterruptedException e) {
            // Empty catch
        }
    }

    // VIOLATION: Duplicate code pattern
    private void sendPush(User user, String message) {
        try {
            Thread.sleep(100);
            System.out.println("Push sent to: " + user.getEmail() + " - " + message);
        } catch (InterruptedException e) {
            // Empty catch
        }
    }

    // VIOLATION: Commented out code
    /*
    public void oldMethod() {
        System.out.println("This is old code");
    }
    */

    // VIOLATION: Dead code
    private void neverCalled() {
        System.out.println("This method is never called");
    }

    // VIOLATION: Complex method with nested conditions
    public String getNotificationPreference(User user) {
        if (user != null) {
            if (user.isActive()) {
                if (user.getRole() != null) {
                    if (user.getRole().equals("PREMIUM")) {
                        if (user.getAccountBalance() > 100) {
                            return "ALL";
                        } else {
                            return "EMAIL_SMS";
                        }
                    } else {
                        return "EMAIL";
                    }
                } else {
                    return "NONE";
                }
            } else {
                return "NONE";
            }
        }
        return "NONE";
    }

    // VIOLATION: Temporary variables with poor names
    public void process(String data) {
        String temp = data;
        String temp2 = temp.toUpperCase();
        String result = temp2.trim();
        String final_result = result.replace(" ", "_");
        System.out.println(final_result);
    }
}
