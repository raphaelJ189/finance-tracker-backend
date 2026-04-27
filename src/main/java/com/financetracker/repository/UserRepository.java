package com.financetracker.repository;


import com.financetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user by email
    Optional<User> findByEmail(String email);

    // Check email if already exists
    boolean existsByEmail(String email);

    // Find active users
    java.util.List<User> findByActiveTrue();

}
