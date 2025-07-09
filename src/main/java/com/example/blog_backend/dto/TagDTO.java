package com.example.blog_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagDTO {
    private Long id;

    @NotBlank(message = "Tag name is required")
    @Size(max = 50, message = "Tag name must not exceed 50 characters")
    private String name;

    @NotBlank(message = "Tag slug is required")
    @Size(max = 50, message = "Tag slug must not exceed 50 characters")
    private String slug;

    private String createdAt;
    private String updatedAt;
    private Long postCount;
}