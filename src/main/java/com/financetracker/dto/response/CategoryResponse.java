package com.financetracker.dto.response;

import com.financetracker.entity.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private TransactionType type;
    private String color;
    private String icon;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;
}