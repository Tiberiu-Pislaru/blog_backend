package com.example.blog_backend.dto;

import com.example.blog_backend.entity.Post;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private String slug;

    @Size(max = 500, message = "Excerpt must not exceed 500 characters")
    private String excerpt;

    @NotBlank(message = "Content is required")
    private String content;

    private String featuredImageUrl;
    private Post.Status status;
    private Long viewCount;
    private String createdAt;
    private String updatedAt;
    private String publishedAt;

    // Author information
    private UserDTO author;

    // Category information
    private CategoryDTO category;

    // Tags
    private List<TagDTO> tags;

    // Comment count
    private Long commentCount;
}