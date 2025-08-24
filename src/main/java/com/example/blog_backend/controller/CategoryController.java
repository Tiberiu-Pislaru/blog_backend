package com.example.blog_backend.controller;

import com.example.blog_backend.dto.ApiResponseDTO;
import com.example.blog_backend.dto.CategoryDTO;
import com.example.blog_backend.dto.PagedResponseDTO;
import com.example.blog_backend.entity.Category;
import com.example.blog_backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public PagedResponseDTO<CategoryDTO> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryDTO> categories = categoryService.findAllCategories(pageable);
        return new PagedResponseDTO<>(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        Optional<Category> category = categoryService.findById(id);
        return category.map(cat ->
                ResponseEntity.ok(CategoryDTO.fromEntity(cat))
        )
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<Category> getCategoryBySlug(@PathVariable String slug) {
        Optional<Category> category = categoryService.findBySlug(slug);
        return category.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ApiResponseDTO<CategoryDTO> createCategory(@Valid @RequestBody Category category) {
        try {
            CategoryDTO createdCategory = categoryService.createCategory(category);
            return ApiResponseDTO.success("Category created successfully.", createdCategory);
        } catch (RuntimeException e) {
            return ApiResponseDTO.error("Error creating category: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @Valid @RequestBody Category categoryDetails) {
        try {
            Category updatedCategory = categoryService.updateCategory(id, categoryDetails);
            return ResponseEntity.ok(updatedCategory);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}