package com.example.blog_backend.dto;

import com.example.blog_backend.entity.Post;
import com.example.blog_backend.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
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
    @NotNull(message = "Author ID is required")
    private Long authorId;
    private String authorName; // Optional, can be used for display purposes
    // Category information
    @NotNull(message = "Category ID is required")
    private Long categoryId;
    private String categoryName; // Optional, can be used for display purposes
    // Tags
    private List<TagDTO> tags;

    // Comment count
    private Long commentCount;

    public Post toEntity() {
        Post post = new Post();
        post.setId(this.id);
        post.setTitle(this.title);
        post.setSlug(this.slug);
        post.setExcerpt(this.excerpt);
        post.setContent(this.content);
        post.setFeaturedImageUrl(this.featuredImageUrl);
        post.setStatus(this.status);
        post.setViewCount(this.viewCount);
        // Convert createdAt, updatedAt, and publishedAt to LocalDateTime if necessary
        post.setCreatedAt(LocalDateTime.parse(this.createdAt));
        post.setUpdatedAt(LocalDateTime.parse(this.updatedAt));
        post.setPublishedAt(LocalDateTime.parse(this.publishedAt));

        // Set author, category, and tags
        if (this.authorId != null) {
            User author = new User();
            author.setId(this.authorId);
            post.setAuthor(author);
        }
        if (this.categoryId != null) {
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setId(this.categoryId);
            post.setCategory(categoryDTO.toEntity());
        }
        if (this.tags != null) {
            post.setTags(this.tags.stream().map(TagDTO::toEntity).toList());
        }
        return post;
    }

    public PostDTO fromEntity(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.slug = post.getSlug();
        this.excerpt = post.getExcerpt();
        this.content = post.getContent();
        this.featuredImageUrl = post.getFeaturedImageUrl();
        this.status = post.getStatus();
        this.viewCount = post.getViewCount();
        this.createdAt = post.getCreatedAt().toString();
        this.updatedAt = post.getUpdatedAt() != null ? post.getUpdatedAt().toString() : null;
        this.publishedAt = post.getPublishedAt() != null ? post.getPublishedAt().toString() : null;

        if (post.getAuthor() != null) {
            this.authorId = post.getAuthor().getId();
            this.authorName = post.getAuthor().getFirstName().concat(" ").concat(post.getAuthor().getLastName()); // Assuming User has a getName() method
        }
        if (post.getCategory() != null) {
            this.categoryId = post.getCategory().getId();
            this.categoryName = post.getCategory().getName(); // Assuming Category has a getName() method
        }
        if (post.getTags() != null) {
            this.tags = post.getTags().stream().map(tag -> new TagDTO().fromEntity(tag)).toList();
        }

        return this;
    }

    public static PostDTO convertToDTO(Post post) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setSlug(post.getSlug());
        dto.setExcerpt(post.getExcerpt());
        dto.setContent(post.getContent());
        dto.setFeaturedImageUrl(post.getFeaturedImageUrl());
        dto.setStatus(post.getStatus()); // if enum
        dto.setViewCount(post.getViewCount());
        dto.setCreatedAt(post.getCreatedAt().toString());
        dto.setUpdatedAt(post.getUpdatedAt().toString());
        if (post.getPublishedAt() != null){
            dto.setPublishedAt(post.getPublishedAt().toString());
        }

        if (post.getAuthor() != null) {
            dto.setAuthorId(post.getAuthor().getId());
            dto.setAuthorName(post.getAuthor().getFirstName() + " " +
                    post.getAuthor().getLastName());
        }

        if (post.getCategory() != null) {
            dto.setCategoryId(post.getCategory().getId());
            dto.setCategoryName(post.getCategory().getName());
        }

        return dto;
    }
}