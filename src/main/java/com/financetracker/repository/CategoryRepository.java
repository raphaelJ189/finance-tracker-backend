package com.financetracker.repository;

import com.financetracker.entity.Category;
import com.financetracker.entity.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Show all active categories
    List<Category> findByUserIdAndActiveTrue(Long userId);

    // Get all active categories for a user filtered by type
    List<Category> findByUserIdAndTypeAndActiveTrue(
            Long userId,
            TransactionType type
    );

    // Check if the category name exists
    boolean existsByUserIdAndNameAndType(
            Long userId,
            String name,
            TransactionType type
    );

    // Find a specific category by id and userId
    Optional<Category> findByIdAndUserId(Long id, Long userId);

    // Count how many active users do category have
    long countByUserIdAndActiveTrue(Long userId);
}
