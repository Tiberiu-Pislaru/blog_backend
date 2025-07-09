package com.example.blog_backend.service;

import com.example.blog_backend.entity.Comment;
import com.example.blog_backend.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;

    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    public Page<Comment> findCommentsByPost(Long postId, Pageable pageable) {
        return commentRepository.findApprovedCommentsByPost(postId, pageable);
    }

    public Page<Comment> findRepliesByParent(Long parentId, Pageable pageable) {
        return commentRepository.findApprovedRepliesByParent(parentId, pageable);
    }

    public Page<Comment> findCommentsByUser(Long userId, Pageable pageable) {
        return commentRepository.findByUserId(userId, pageable);
    }

    public Page<Comment> findPendingComments(Pageable pageable) {
        return commentRepository.findPendingComments(pageable);
    }

    public Comment approveComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setIsApproved(true);
        return commentRepository.save(comment);
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    public Long getCommentCount(Long postId) {
        return commentRepository.countApprovedCommentsByPost(postId);
    }
}
