package com.financetracker.service;

import com.financetracker.dto.request.CategoryRequest;
import com.financetracker.dto.response.CategoryResponse;
import com.financetracker.entity.Category;
import com.financetracker.entity.User;
import com.financetracker.exception.BusinessException;
import com.financetracker.exception.DuplicateResourceException;
import com.financetracker.exception.ResourceNotFoundException;
import com.financetracker.mapper.CategoryMapper;
import com.financetracker.repository.CategoryRepository;
import com.financetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryMapper categoryMapper;

    // Get all active categories for a current user
    @Transactional(readOnly = true)
    public List<CategoryResponse> getMyCategories(User currentUser) {
        log.debug("Fetching categories for userId: {}", currentUser.getId());

        List<Category> categories = categoryRepository.findByUserIdAndActiveTrue(currentUser.getId());

        return categoryMapper.toResponseList(categories);
    }

    // Get a single category by id
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id, User currentUser) {
        log.debug("Fetching category id: {} for userId: {}", id, currentUser.getId());

        Category category = findCategoryOwnedByUser(id, currentUser.getId());
        return categoryMapper.toResponse(category);
    }

    // Create a new category
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request,
                                           User currentUser) {
        log.info("Creating category: name={}, type={}, userId={}",
                request.getName(), request.getType(), currentUser.getId());

        // Check if a user already has a category with the same name and type
        if (categoryRepository.existsByUserIdAndNameAndType(
                currentUser.getId(),
                request.getName(),
                request.getType())) {
            throw new DuplicateResourceException(
                    "Category", "name", request.getName()
            );
        }

        // Build category with defaults for optional fields
        Category category = Category.builder()
                .user(currentUser)
                .name(request.getName())
                .type(request.getType())
                .color(request.getColor() != null ? request.getColor() : "#6366F1")
                .icon(request.getIcon() != null ? request.getIcon() : "tag")
                .description(request.getDescription())
                .active(true)
                .build();

        Category savedCategory = categoryRepository.save(category);

        log.info("Category created: id={}, name={}, userId={}",
                savedCategory.getId(),
                savedCategory.getName(),
                currentUser.getId());

        return categoryMapper.toResponse(savedCategory);
    }

    // Update an existing category
    @Transactional
    public CategoryResponse updateCategory(Long id,
                                           CategoryRequest request,
                                           User currentUser) {
        log.info("Updating category id: {} for userId: {}",
                id, currentUser.getId());

        Category category = findCategoryOwnedByUser(id, currentUser.getId());

        // Check if a new name conflicts with another category
        if (!category.getName().equals(request.getName()) &&
                categoryRepository.existsByUserIdAndNameAndType(
                        currentUser.getId(),
                        request.getName(),
                        request.getType())) {
            throw new DuplicateResourceException(
                    "Category", "name", request.getName()
            );
        }

        // Update fields
        category.setName(request.getName());
        category.setType(request.getType());

        if (request.getColor() != null) {
            category.setColor(request.getColor());
        }
        if (request.getIcon() != null) {
            category.setIcon(request.getIcon());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        Category updatedCategory = categoryRepository.save(category);

        log.info("Category updated: id={}", updatedCategory.getId());

        return categoryMapper.toResponse(updatedCategory);
    }

    // Soft delete a category
    @Transactional
    public void deleteCategory(Long id, User currentUser) {
        log.info("Soft deleting category id: {} for userId: {}", id, currentUser.getId());

        Category category = findCategoryOwnedByUser(id, currentUser.getId());

        // Business rule: cannot deactivate a category that has transactions
        // Check if any non-deleted transactions use this category
        long transactionCount = transactionRepository
                .countByCategoryIdAndDeletedFalse(id);

        if (transactionCount > 0) {
            throw new BusinessException(
                    "Cannot delete category that has "
                            + transactionCount
                            + " existing transaction(s). "
                            + "Please reassign or delete the transactions first."
            );
        }

        // A Soft delete — set active to false
        category.setActive(false);
        categoryRepository.save(category);

        log.info("Category soft deleted: id={}", id);
    }

    private Category findCategoryOwnedByUser(Long categoryId, Long userId) {
        return categoryRepository
                .findByIdAndUserId(categoryId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category", categoryId));
    }
}