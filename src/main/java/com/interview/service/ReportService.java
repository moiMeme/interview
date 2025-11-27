package com.interview.service;

import com.interview.model.Order;
import com.interview.model.User;
import com.interview.repository.OrderRepository;
import com.interview.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// VIOLATION: Multiple threading issues and race conditions
@Service
public class ReportService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    // VIOLATION: Shared mutable state without synchronization
    private Map<String, Integer> reportCache = new HashMap<>();
    private List<String> processingQueue = new ArrayList<>();
    private int reportCount = 0;

    // VIOLATION: Thread pool created but never shutdown - resource leak
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    // VIOLATION: Race condition on shared state
    public void generateAsyncReport(Long userId) {
        executorService.submit(() -> {
            try {
                // VIOLATION: Non-atomic check-then-act
                if (reportCount < 100) {
                    reportCount++; // VIOLATION: Race condition

                    User user = userRepository.findById(userId).orElse(null);
                    if (user != null) {
                        // VIOLATION: Modifying shared collection without synchronization
                        processingQueue.add(user.getEmail());

                        List<Order> orders = orderRepository.findByUserId(userId);

                        // VIOLATION: Race condition on HashMap
                        reportCache.put(user.getEmail(), orders.size());

                        // Simulate processing
                        Thread.sleep(1000);

                        // VIOLATION: Race condition on ArrayList
                        processingQueue.remove(user.getEmail());
                    }
                }
            } catch (Exception e) {
                // VIOLATION: Swallowing exceptions in thread
                System.err.println("Error in async report: " + e.getMessage());
            }
        });
    }

    // VIOLATION: Incorrect double-checked locking
    private volatile Object lock = new Object();

    public void incrementReportCount() {
        if (reportCount < 1000) { // VIOLATION: Check outside synchronized block
            synchronized (lock) {
                reportCount++; // VIOLATION: Still a race condition
            }
        }
    }

    // VIOLATION: Synchronizing on non-final field
    private Object syncObject = new Object();

    public void updateCache(String key, int value) {
        synchronized (syncObject) {
            reportCache.put(key, value);
        }
    }

    // VIOLATION: Method accesses shared state without synchronization
    public int getReportCount() {
        return reportCount; // VIOLATION: Reading shared mutable state without sync
    }

    // VIOLATION: Returning reference to mutable internal state
    public List<String> getProcessingQueue() {
        return processingQueue; // VIOLATION: Direct reference to internal collection
    }

    // VIOLATION: Race condition with multiple threads
    public Map<String, Double> calculateTotalRevenue() {
        Map<String, Double> revenue = new HashMap<>();

        List<User> users = userRepository.findAll();

        // VIOLATION: Creating threads manually instead of using thread pool properly
        for (User user : users) {
            new Thread(() -> {
                List<Order> orders = orderRepository.findByUserId(user.getId());
                double total = orders.stream()
                        .mapToDouble(Order::getAmount)
                        .sum();

                // VIOLATION: Race condition on HashMap access
                revenue.put(user.getEmail(), total);
            }).start();
        }

        // VIOLATION: Returning result immediately before threads complete
        return revenue; // Will likely return incomplete/empty results
    }

    // VIOLATION: Deadlock potential with nested locks
    private final Object lockA = new Object();
    private final Object lockB = new Object();

    public void method1() {
        synchronized (lockA) {
            System.out.println("Method1: Locked A");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            synchronized (lockB) {
                System.out.println("Method1: Locked B");
            }
        }
    }

    public void method2() {
        synchronized (lockB) {
            System.out.println("Method2: Locked B");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            synchronized (lockA) {
                System.out.println("Method2: Locked A");
            }
        }
    }

    // VIOLATION: Lazy initialization race condition
    private List<String> reportTemplates;

    public List<String> getReportTemplates() {
        if (reportTemplates == null) { // VIOLATION: Not thread-safe
            reportTemplates = new ArrayList<>();
            reportTemplates.add("Template1");
            reportTemplates.add("Template2");
        }
        return reportTemplates;
    }

    // VIOLATION: Static mutable field accessed by multiple threads
    public static int globalReportId = 0;

    public int generateReportId() {
        return ++globalReportId; // VIOLATION: Not atomic
    }

    // VIOLATION: Sleep in production code
    public void waitForReport() {
        try {
            Thread.sleep(5000); // VIOLATION: Blocking for fixed time
        } catch (InterruptedException e) {
            // VIOLATION: Ignoring interruption
        }
    }
}
