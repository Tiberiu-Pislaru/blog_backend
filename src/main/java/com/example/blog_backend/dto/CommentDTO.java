package com.example.blog_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;

    @NotBlank(message = "Comment content is required")
    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    private String content;

    private Boolean isApproved;
    private String createdAt;
    private String updatedAt;

    // User information
    private UserDTO user;

    // Post information (minimal)
    private Long postId;
    private String postTitle;

    // Parent comment (for replies)
    private Long parentCommentId;

    // Replies to this comment
    private List<CommentDTO> replies;
}