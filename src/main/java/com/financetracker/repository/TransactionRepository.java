package com.financetracker.repository;

import com.financetracker.entity.Transaction;
import com.financetracker.entity.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Find all non-deleted transactions for a user
    Page<Transaction> findByUserIdAndDeletedFalse(
            Long userId,
            Pageable pageable
    );

    // Find a specific transaction by id and user id

    Optional<Transaction> findByIdAndUserIdAndDeletedFalse(
            Long id,
            Long userId
    );

    // Calculate total amount by type for a user
    // Used for summary — total income or total expenses
    // @Query uses JPQL — works on entity field names
    @Query("SELECT COALESCE(SUM(t.amount), 0) " +
            "FROM Transaction t " +
            "WHERE t.user.id = :userId " +
            "AND t.type = :type " +
            "AND t.deleted = false")
    BigDecimal sumAmountByUserIdAndType(
            @Param("userId") Long userId,
            @Param("type") TransactionType type
    );

    // Calculate total amount by type for a user within a date range
    // Used for monthly summary
    @Query("SELECT COALESCE(SUM(t.amount), 0) " +
            "FROM Transaction t " +
            "WHERE t.user.id = :userId " +
            "AND t.type = :type " +
            "AND t.deleted = false " +
            "AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByUserIdAndTypeAndDateRange(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Calculate total spending in a specific category
    // for a specific month and year
    // Used for budget vs actual comparison
    @Query("SELECT COALESCE(SUM(t.amount), 0) " +
            "FROM Transaction t " +
            "WHERE t.user.id = :userId " +
            "AND t.category.id = :categoryId " +
            "AND t.type = 'EXPENSE' " +
            "AND t.deleted = false " +
            "AND MONTH(t.transactionDate) = :month " +
            "AND YEAR(t.transactionDate) = :year")
    BigDecimal sumExpensesByCategoryAndMonthYear(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("month") int month,
            @Param("year") int year
    );

    long countByCategoryIdAndDeletedFalse(Long categoryId);
}