package com.financetracker.controller.v1;

import com.financetracker.dto.request.TransactionRequest;
import com.financetracker.dto.response.PageResponse;
import com.financetracker.dto.response.SummaryResponse;
import com.financetracker.dto.response.TransactionResponse;
import com.financetracker.entity.User;
import com.financetracker.service.SummaryService;
import com.financetracker.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;
    private final SummaryService summaryService;

    // Get all transactions with pagination
    @GetMapping
    public ResponseEntity<PageResponse<TransactionResponse>> getMyTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User currentUser) {

        Page<TransactionResponse> transactions =
                transactionService.getMyTransactions(currentUser, page, size);

        return ResponseEntity.ok(PageResponse.of(transactions));
    }

    // Get financial summary
    @GetMapping("/summary")
    public ResponseEntity<SummaryResponse> getMySummary(
            @AuthenticationPrincipal User currentUser) {

        SummaryResponse response = summaryService.getMySummary(currentUser);
        return ResponseEntity.ok(response);
    }

    // Get single transaction
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        TransactionResponse response =
                transactionService.getTransactionById(id, currentUser);
        return ResponseEntity.ok(response);
    }

    // Create transaction
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest request,
            @AuthenticationPrincipal User currentUser) {

        TransactionResponse response =
                transactionService.createTransaction(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Update transaction
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request,
            @AuthenticationPrincipal User currentUser) {

        TransactionResponse response =
                transactionService.updateTransaction(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

    // Delete transaction (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        transactionService.deleteTransaction(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}