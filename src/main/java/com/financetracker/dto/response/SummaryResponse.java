package com.financetracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SummaryResponse {

    // Overall totals — all time
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal balance;
    // balance = totalIncome - totalExpenses

    // Current month totals
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyExpenses;
    private BigDecimal monthlyBalance;

    // Current month info
    private int currentMonth;
    private int currentYear;
    private String currency;
}