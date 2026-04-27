package com.financetracker.dto.request;

import com.financetracker.entity.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Category type is required")
    private TransactionType type;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Color must be a valid hex color code")
    private String color;

    @Size(max = 50, message = "Icon name cannot exceed 50 characters")
    private String icon;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
}