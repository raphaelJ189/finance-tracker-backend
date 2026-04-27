package com.financetracker.service;

import com.financetracker.dto.request.BudgetRequest;
import com.financetracker.dto.response.BudgetResponse;
import com.financetracker.entity.Budget;
import com.financetracker.entity.Category;
import com.financetracker.entity.User;
import com.financetracker.exception.BusinessException;
import com.financetracker.exception.DuplicateResourceException;
import com.financetracker.exception.ResourceNotFoundException;
import com.financetracker.mapper.BudgetMapper;
import com.financetracker.repository.BudgetRepository;
import com.financetracker.repository.CategoryRepository;
import com.financetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetMapper budgetMapper;

    // Get all budgets for the current user in a specific month and year
    // Also calculates actual spending for each budget
    @Transactional(readOnly = true)
    public List<BudgetResponse> getMyBudgets(User currentUser,
                                             int month,
                                             int year) {
        log.debug("Fetching budgets for userId: {}, month: {}, year: {}",
                currentUser.getId(), month, year);

        // Fetch budgets with actual spending in one query
        List<Object[]> results = budgetRepository.findBudgetsWithSpending(
                currentUser.getId(), month, year);

        // Map each result to BudgetResponse
        return results.stream()
                .map(result -> {
                    Budget budget = (Budget) result[0];
                    BigDecimal spent = (BigDecimal) result[1];

                    return budgetMapper.toResponse(budget, spent);
                })
                .collect(Collectors.toList());
    }

    // Get a single budget by id
    @Transactional(readOnly = true)
    public BudgetResponse getBudgetById(Long id, User currentUser) {
        log.debug("Fetching budget id: {} for userId: {}",
                id, currentUser.getId());

        Budget budget = findBudgetOwnedByUser(id, currentUser.getId());

        // Calculate spending for this specific budget
        BigDecimal spent = transactionRepository
                .sumExpensesByCategoryAndMonthYear(
                        currentUser.getId(),
                        budget.getCategory().getId(),
                        budget.getMonth(),
                        budget.getYear()
                );

        return budgetMapper.toResponse(budget, spent);
    }

    // Create a new budget
    @Transactional
    public BudgetResponse createBudget(BudgetRequest request,
                                       User currentUser) {
        log.info("Creating budget: categoryId={}, month={}, year={}, userId={}",
                request.getCategoryId(), request.getMonth(),
                request.getYear(), currentUser.getId());

        // Verify category exists and belongs to user
        Category category = categoryRepository
                .findByIdAndUserId(request.getCategoryId(), currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category", request.getCategoryId()
                ));

        // Business rule:  a budget only makes sense for EXPENSE categories
        if (category.getType().name().equals("INCOME")) {
            throw new BusinessException(
                    "Cannot set a budget for an income category. "
                            + "Budgets are only for expense categories."
            );
        }

        // Check if a budget already exists for this combination
        if (budgetRepository.existsByUserIdAndCategoryIdAndMonthAndYear(
                currentUser.getId(),
                request.getCategoryId(),
                request.getMonth(),
                request.getYear())) {
            throw new DuplicateResourceException(
                    "Budget",
                    "category/month/year",
                    category.getName() + " - "
                            + request.getMonth() + "/" + request.getYear()
            );
        }

        Budget budget = Budget.builder()
                .user(currentUser)
                .category(category)
                .amount(request.getAmount())
                .month(request.getMonth())
                .year(request.getYear())
                .build();

        Budget savedBudget = budgetRepository.save(budget);

        log.info("Budget created: id={}, userId={}",
                savedBudget.getId(), currentUser.getId());

        // New budget has zero spending
        return budgetMapper.toResponse(savedBudget, BigDecimal.ZERO);
    }

    // Update budget amount
    @Transactional
    public BudgetResponse updateBudget(Long id,
                                       BudgetRequest request,
                                       User currentUser) {
        log.info("Updating budget id: {} for userId: {}",
                id, currentUser.getId());

        Budget budget = findBudgetOwnedByUser(id, currentUser.getId());

        // Update the amount
        budget.setAmount(request.getAmount());
        Budget updatedBudget = budgetRepository.save(budget);

        // Recalculate spending after update
        BigDecimal spent = transactionRepository
                .sumExpensesByCategoryAndMonthYear(
                        currentUser.getId(),
                        updatedBudget.getCategory().getId(),
                        updatedBudget.getMonth(),
                        updatedBudget.getYear()
                );

        log.info("Budget updated: id={}", updatedBudget.getId());

        return budgetMapper.toResponse(updatedBudget, spent);
    }

    // Hard delete a budget
    @Transactional
    public void deleteBudget(Long id, User currentUser) {
        log.info("Deleting budget id: {} for userId: {}",
                id, currentUser.getId());

        Budget budget = findBudgetOwnedByUser(id, currentUser.getId());
        budgetRepository.delete(budget);

        log.info("Budget deleted: id={}", id);
    }

    // Private helper :finds budget and verifies ownership
    private Budget findBudgetOwnedByUser(Long budgetId, Long userId) {
        return budgetRepository
                .findByIdAndUserId(budgetId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Budget", budgetId));
    }
}