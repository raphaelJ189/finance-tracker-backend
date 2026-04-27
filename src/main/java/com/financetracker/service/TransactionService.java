package com.financetracker.service;

import com.financetracker.dto.request.TransactionRequest;
import com.financetracker.dto.response.TransactionResponse;
import com.financetracker.entity.Category;
import com.financetracker.entity.Transaction;
import com.financetracker.entity.User;
import com.financetracker.exception.BusinessException;
import com.financetracker.exception.ResourceNotFoundException;
import com.financetracker.mapper.TransactionMapper;
import com.financetracker.repository.CategoryRepository;
import com.financetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;

    // Get all transactions for a current user with pagination
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getMyTransactions(User currentUser, int page, int size) {
        log.debug("Fetching transactions for userId: {}, page: {}, size: {}",
                currentUser.getId(), page, size);

        // Cap maximum page size to prevent
        int validSize = Math.min(size, 100);

        // Sort by transaction date descending
        // Most recent transactions first
        Pageable pageable = PageRequest.of(
                page,
                validSize,
                Sort.by("transactionDate").descending()
        );

        Page<Transaction> transactions = transactionRepository
                .findByUserIdAndDeletedFalse(currentUser.getId(), pageable);

        return transactions.map(transactionMapper::toResponse);
    }

    // Get a single transaction by id
    // Verifies ownership
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long id, User currentUser) {
        log.debug("Fetching transaction id: {} for userId: {}",
                id, currentUser.getId());

        Transaction transaction = findTransactionOwnedByUser(
                id, currentUser.getId()
        );
        return transactionMapper.toResponse(transaction);
    }

    // Create a new transaction
    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request,
                                                 User currentUser) {
        log.info("Creating transaction: amount={}, type={}, userId={}",
                request.getAmount(), request.getType(), currentUser.getId());

        // Verify the category exists and belongs to this user
        Category category = categoryRepository
                .findByIdAndUserId(request.getCategoryId(), currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category", request.getCategoryId()
                ));

        // Business rule: a transaction type must match a category type
        if (!category.getType().equals(request.getType())) {
            throw new BusinessException(
                    "Transaction type '"
                            + request.getType()
                            + "' does not match category type '"
                            + category.getType()
                            + "'. Please select the correct category."
            );
        }

        // Build the transaction
        Transaction transaction = Transaction.builder()
                .user(currentUser)
                .category(category)
                .amount(request.getAmount())
                .type(request.getType())
                .description(request.getDescription())
                .referenceNumber(request.getReferenceNumber())
                .transactionDate(request.getTransactionDate())
                .notes(request.getNotes())
                .deleted(false)
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info("Transaction created: id={}, amount={}, userId={}",
                savedTransaction.getId(),
                savedTransaction.getAmount(),
                currentUser.getId());

        return transactionMapper.toResponse(savedTransaction);
    }

    // Update an existing transaction
    @Transactional
    public TransactionResponse updateTransaction(Long id,
                                                 TransactionRequest request,
                                                 User currentUser) {
        log.info("Updating transaction id: {} for userId: {}",
                id, currentUser.getId());

        Transaction transaction = findTransactionOwnedByUser(
                id, currentUser.getId()
        );

        // Verify a new category exists and belongs to a user
        Category category = categoryRepository
                .findByIdAndUserId(request.getCategoryId(), currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category", request.getCategoryId()
                ));

        // Business rule: a type must match a category type
        if (!category.getType().equals(request.getType())) {
            throw new BusinessException(
                    "Transaction type '"
                            + request.getType()
                            + "' does not match category type '"
                            + category.getType()
                            + "'."
            );
        }

        // Update all fields
        transaction.setCategory(category);
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setDescription(request.getDescription());
        transaction.setReferenceNumber(request.getReferenceNumber());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setNotes(request.getNotes());

        Transaction updatedTransaction = transactionRepository.save(transaction);

        log.info("Transaction updated: id={}", updatedTransaction.getId());

        return transactionMapper.toResponse(updatedTransaction);
    }

    // Soft delete a transaction
    // Sets is_deleted = true instead of removing from database
    // Preserves financial history
    @Transactional
    public void deleteTransaction(Long id, User currentUser) {
        log.info("Soft deleting transaction id: {} for userId: {}",
                id, currentUser.getId());

        Transaction transaction = findTransactionOwnedByUser(
                id, currentUser.getId()
        );

        transaction.setDeleted(true);
        transactionRepository.save(transaction);

        log.info("Transaction soft deleted: id={}", id);
    }

    // Private helper : finds a transaction and verifies ownership
    private Transaction findTransactionOwnedByUser(Long transactionId,
                                                   Long userId) {
        return transactionRepository
                .findByIdAndUserIdAndDeletedFalse(transactionId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction", transactionId
                        ));
    }
}