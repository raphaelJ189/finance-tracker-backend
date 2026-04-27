package com.financetracker.mapper;

import com.financetracker.dto.response.BudgetResponse;
import com.financetracker.entity.Budget;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BudgetMapper {

    // Convert Budget entity to BudgetResponse DTO
    // spent is calculated in the service and passed here
    // remaining and exceeded are calculated here
    public BudgetResponse toResponse(Budget budget, BigDecimal spent) {

        BigDecimal remaining = budget.getAmount().subtract(spent);
        // remaining = budget limit - actual spending
        // positive = under budget
        // negative = over budget

        boolean exceeded = spent.compareTo(budget.getAmount()) > 0;
        return BudgetResponse.builder()
                .id(budget.getId())
                .amount(budget.getAmount())
                .spent(spent)
                .remaining(remaining)
                .exceeded(exceeded)
                .month(budget.getMonth())
                .year(budget.getYear())

                // Category details
                .categoryId(budget.getCategory().getId())
                .categoryName(budget.getCategory().getName())
                .categoryColor(budget.getCategory().getColor())
                .categoryIcon(budget.getCategory().getIcon())
                .categoryType(budget.getCategory().getType())
                .createdAt(budget.getCreatedAt())
                .build();
    }
}