package com.example.blog_backend.repository;

import com.example.blog_backend.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.isApproved = true AND c.parentComment IS NULL ORDER BY c.createdAt DESC")
    Page<Comment> findApprovedCommentsByPost(@Param("postId") Long postId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.parentComment.id = :parentId AND c.isApproved = true ORDER BY c.createdAt ASC")
    Page<Comment> findApprovedRepliesByParent(@Param("parentId") Long parentId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    Page<Comment> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.isApproved = false ORDER BY c.createdAt DESC")
    Page<Comment> findPendingComments(Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId AND c.isApproved = true")
    Long countApprovedCommentsByPost(@Param("postId") Long postId);
}
