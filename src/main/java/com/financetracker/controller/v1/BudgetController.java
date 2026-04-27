package com.financetracker.controller.v1;

import com.financetracker.dto.request.BudgetRequest;
import com.financetracker.dto.response.BudgetResponse;
import com.financetracker.entity.User;
import com.financetracker.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
@Slf4j
public class BudgetController {

    private final BudgetService budgetService;

    // Get all budgets for current month by default
    // Can specify month and year as query params
    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getMyBudgets(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @AuthenticationPrincipal User currentUser) {

        // Default to current month and year if not provided
        int targetMonth = month != null ? month : LocalDate.now().getMonthValue();
        int targetYear = year != null ? year : LocalDate.now().getYear();

        List<BudgetResponse> response =
                budgetService.getMyBudgets(currentUser, targetMonth, targetYear);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> getBudgetById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        BudgetResponse response =
                budgetService.getBudgetById(id, currentUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(
            @Valid @RequestBody BudgetRequest request,
            @AuthenticationPrincipal User currentUser) {

        BudgetResponse response =
                budgetService.createBudget(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request,
            @AuthenticationPrincipal User currentUser) {

        BudgetResponse response =
                budgetService.updateBudget(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        budgetService.deleteBudget(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}