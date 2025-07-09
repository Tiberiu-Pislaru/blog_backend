package com.example.blog_backend.repository;

import com.example.blog_backend.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findBySlug(String slug);
    Optional<Tag> findByName(String name);
    boolean existsBySlug(String slug);
    boolean existsByName(String name);

    @Query("SELECT t FROM Tag t ORDER BY t.name ASC")
    Page<Tag> findAllOrderByName(Pageable pageable);
}
