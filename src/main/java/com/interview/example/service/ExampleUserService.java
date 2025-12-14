package com.interview.example.service;

import com.interview.example.model.ExampleUser;
import com.interview.example.repository.ExampleUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExampleUserService {

    private final ExampleUserRepository userRepository;

    public ExampleUserService(ExampleUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<ExampleUser> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<ExampleUser> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public ExampleUser createUser(ExampleUser user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    public ExampleUser updateUser(Long id, ExampleUser userDetails) {
        ExampleUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        // Check if email is being changed and if it already exists
        if (!user.getEmail().equals(userDetails.getEmail()) &&
                userRepository.existsByEmail(userDetails.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userDetails.getEmail());
        }

        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setPhoneNumber(userDetails.getPhoneNumber());

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
