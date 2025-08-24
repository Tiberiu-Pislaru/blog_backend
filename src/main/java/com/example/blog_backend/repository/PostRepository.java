package com.example.blog_backend.repository;

import com.example.blog_backend.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findBySlug(String slug);

    @Query("SELECT p " +
            "FROM Post p " +
            "WHERE p.status = 'PUBLISHED' ORDER BY p.publishedAt DESC")
    Page<Post> findPublishedPosts(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.author.id = :authorId ORDER BY p.createdAt DESC")
    Page<Post> findByAuthorId(@Param("authorId") Long authorId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.category.id = :categoryId AND p.status = 'PUBLISHED'")
    Page<Post> findPublishedPostsByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t.id = :tagId AND p.status = 'PUBLISHED'")
    Page<Post> findPublishedPostsByTag(@Param("tagId") Long tagId, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "WHERE p.status = 'PUBLISHED' " +
            "AND (:keyword IS NULL OR :keyword = '' " +
            "OR p.title LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR p.content LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:categoryId IS NULL OR p.category.id = :categoryId)")
    Page<Post> findPublishedPostsByKeyword(@Param("keyword") String keyword, Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' ORDER BY p.viewCount DESC")
    Page<Post> findMostViewedPosts(Pageable pageable);

    // Trova un numero limitato di articoli in evidenza
    @Query(value = "SELECT p " +
            "FROM Post p " +
            "WHERE p.isFeatured = true " +
            "ORDER BY p.createdAt DESC " +
            "LIMIT :limit")
    List<Post> findTopFeaturedPosts(@Param("limit") int limit);

    boolean existsBySlug(String slug);
}