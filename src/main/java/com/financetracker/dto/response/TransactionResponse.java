package com.financetracker.dto.response;

import com.financetracker.entity.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
    private String referenceNumber;
    private LocalDate transactionDate;
    private String notes;
    private LocalDateTime createdAt;

    // Category details in response
    private Long categoryId;
    private String categoryName;
    private String categoryColor;
    private String categoryIcon;
}