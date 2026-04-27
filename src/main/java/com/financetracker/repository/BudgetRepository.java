package com.financetracker.repository;

import com.financetracker.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    // Get all budgets for a user in a specific month and year
    List<Budget> findByUserIdAndMonthAndYear(
            Long userId,
            int month,
            int year
    );

    // Find a specific budget by id and user id
    Optional<Budget> findByIdAndUserId(Long id, Long userId);

    // Check if a budget already exists for this combination
    // Used during budget creation to prevent duplicates
    boolean existsByUserIdAndCategoryIdAndMonthAndYear(
            Long userId,
            Long categoryId,
            int month,
            int year
    );

    // Find a specific budget by user, category, month, year
    // Used when updating a budget
    // Also used for budget vs actual calculation
    Optional<Budget> findByUserIdAndCategoryIdAndMonthAndYear(
            Long userId,
            Long categoryId,
            int month,
            int year
    );

    // Get budgets with their actual spending
    // For each budget, it also calculates how much was spent
    // in that category in that month
    @Query("SELECT b, " +
            "COALESCE(SUM(t.amount), 0) as spent " +
            "FROM Budget b " +
            "LEFT JOIN Transaction t " +
            "ON t.category.id = b.category.id " +
            "AND t.user.id = b.user.id " +
            "AND t.deleted = false " +
            "AND t.type = 'EXPENSE' " +
            "AND MONTH(t.transactionDate) = b.month " +
            "AND YEAR(t.transactionDate) = b.year " +
            "WHERE b.user.id = :userId " +
            "AND b.month = :month " +
            "AND b.year = :year " +
            "GROUP BY b")
    List<Object[]> findBudgetsWithSpending(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year
    );
}