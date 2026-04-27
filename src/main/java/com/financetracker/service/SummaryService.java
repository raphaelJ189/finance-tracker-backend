package com.financetracker.service;

import com.financetracker.dto.response.SummaryResponse;
import com.financetracker.entity.User;
import com.financetracker.entity.enums.TransactionType;
import com.financetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class SummaryService {

    private final TransactionRepository transactionRepository;

    // Get financial summary for current user
    // Includes all time totals and current month totals
    @Transactional(readOnly = true)
    public SummaryResponse getMySummary(User currentUser) {
        log.debug("Calculating summary for userId: {}", currentUser.getId());

        Long userId = currentUser.getId();

        // All time totals
        BigDecimal totalIncome = transactionRepository
                .sumAmountByUserIdAndType(userId, TransactionType.INCOME);

        BigDecimal totalExpenses = transactionRepository
                .sumAmountByUserIdAndType(userId, TransactionType.EXPENSE);

        BigDecimal balance = totalIncome.subtract(totalExpenses);

        // Current month date range
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        // First day of current month
        LocalDate endOfMonth = now.withDayOfMonth(
                now.getMonth().length(now.isLeapYear())
        );
        // Last day of current month
        // isLeapYear handles February correctly

        // Current month totals
        BigDecimal monthlyIncome = transactionRepository
                .sumAmountByUserIdAndTypeAndDateRange(
                        userId,
                        TransactionType.INCOME,
                        startOfMonth,
                        endOfMonth
                );

        BigDecimal monthlyExpenses = transactionRepository
                .sumAmountByUserIdAndTypeAndDateRange(
                        userId,
                        TransactionType.EXPENSE,
                        startOfMonth,
                        endOfMonth
                );

        BigDecimal monthlyBalance = monthlyIncome.subtract(monthlyExpenses);

        log.debug("Summary calculated for userId: {} " +
                        "totalIncome={}, totalExpenses={}, balance={}",
                userId, totalIncome, totalExpenses, balance);

        return SummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .balance(balance)
                .monthlyIncome(monthlyIncome)
                .monthlyExpenses(monthlyExpenses)
                .monthlyBalance(monthlyBalance)
                .currentMonth(now.getMonthValue())
                .currentYear(now.getYear())
                .currency(currentUser.getCurrency())
                .build();
    }
}