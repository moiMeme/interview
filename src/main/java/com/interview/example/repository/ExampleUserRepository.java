package com.interview.example.repository;

import com.interview.example.model.ExampleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExampleUserRepository extends JpaRepository<ExampleUser, Long> {

    Optional<ExampleUser> findByEmail(String email);

    boolean existsByEmail(String email);
}
