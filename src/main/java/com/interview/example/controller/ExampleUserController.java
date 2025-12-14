package com.interview.example.controller;

import com.interview.example.model.ExampleUser;
import com.interview.example.service.ExampleUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/example/v1/users")
@CrossOrigin(origins = "*")
public class ExampleUserController {

    private final ExampleUserService userService;

    public ExampleUserController(ExampleUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<ExampleUser>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExampleUser> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ExampleUser> createUser(@Valid @RequestBody ExampleUser user) {
        try {
            ExampleUser createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExampleUser> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody ExampleUser userDetails) {
        try {
            ExampleUser updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
