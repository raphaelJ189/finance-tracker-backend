package com.financetracker.mapper;

import com.financetracker.dto.response.TransactionResponse;
import com.financetracker.entity.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransactionMapper {

    // Coonvert Transaction entity to TransactionResponse DTO
    // Notice we flatten category details into the response
    // Frontend gets everything in one object
    public TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .description(transaction.getDescription())
                .referenceNumber(transaction.getReferenceNumber())
                .transactionDate(transaction.getTransactionDate())
                .notes(transaction.getNotes())
                .createdAt(transaction.getCreatedAt())
                // Category details — flattened into response
                .categoryId(transaction.getCategory().getId())
                .categoryName(transaction.getCategory().getName())
                .categoryColor(transaction.getCategory().getColor())
                .categoryIcon(transaction.getCategory().getIcon())
                .build();
    }

    // Convert a list of Transaction entities to a list of DTOs
    public List<TransactionResponse> toResponseList(List<Transaction> transactions) {
        return transactions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}