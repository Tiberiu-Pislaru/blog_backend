package com.example.blog_backend.dto;

import com.example.blog_backend.entity.Category;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long id;

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Category slug is required")
    @Size(max = 100, message = "Category slug must not exceed 100 characters")
    private String slug;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private String createdAt;
    private String updatedAt;

    public Category toEntity() {
        Category category = new Category();
        category.setId(this.id);
        category.setName(this.name);
        category.setSlug(this.slug);
        category.setDescription(this.description);
        return category;
    }

    public static CategoryDTO fromEntity(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId( category.getId());
        categoryDTO.setName(category.getName());
        categoryDTO.setSlug(category.getSlug());
        categoryDTO.setDescription( category.getDescription());
        categoryDTO.setCreatedAt(category.getCreatedAt().toString());
        categoryDTO.setUpdatedAt(category.getUpdatedAt().toString());
        return categoryDTO;
    }
}