package com.financetracker.dto.response;

import com.financetracker.entity.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetResponse {

    private Long id;
    private BigDecimal amount;
    private BigDecimal spent;
    private BigDecimal remaining;
    private boolean exceeded;
    private int month;
    private int year;

    // Category details
    private Long categoryId;
    private String categoryName;
    private String categoryColor;
    private String categoryIcon;
    private TransactionType categoryType;
    private LocalDateTime createdAt;
}