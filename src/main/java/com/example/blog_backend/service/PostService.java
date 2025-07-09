package com.example.blog_backend.service;

import com.example.blog_backend.entity.Post;
import com.example.blog_backend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;

    public Post createPost(Post post) {
        if (postRepository.existsBySlug(post.getSlug())) {
            throw new RuntimeException("Slug already exists");
        }
        return postRepository.save(post);
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    public Optional<Post> findBySlug(String slug) {
        return postRepository.findBySlug(slug);
    }

    public Page<Post> findPublishedPosts(Pageable pageable) {
        return postRepository.findPublishedPosts(pageable);
    }

    public Page<Post> findPostsByAuthor(Long authorId, Pageable pageable) {
        return postRepository.findByAuthorId(authorId, pageable);
    }

    public Page<Post> findPostsByCategory(Long categoryId, Pageable pageable) {
        return postRepository.findPublishedPostsByCategory(categoryId, pageable);
    }

    public Page<Post> findPostsByTag(Long tagId, Pageable pageable) {
        return postRepository.findPublishedPostsByTag(tagId, pageable);
    }

    public Page<Post> searchPosts(String keyword, Pageable pageable) {
        return postRepository.findPublishedPostsByKeyword(keyword, pageable);
    }

    public Page<Post> findMostViewedPosts(Pageable pageable) {
        return postRepository.findMostViewedPosts(pageable);
    }

    public Post updatePost(Long id, Post postDetails) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setTitle(postDetails.getTitle());
        post.setSlug(postDetails.getSlug());
        post.setExcerpt(postDetails.getExcerpt());
        post.setContent(postDetails.getContent());
        post.setFeaturedImageUrl(postDetails.getFeaturedImageUrl());
        post.setStatus(postDetails.getStatus());
        post.setCategory(postDetails.getCategory());
        post.setTags(postDetails.getTags());

        if (postDetails.getStatus() == Post.Status.PUBLISHED && post.getPublishedAt() == null) {
            post.setPublishedAt(LocalDateTime.now());
        }

        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public Post incrementViewCount(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setViewCount(post.getViewCount() + 1);
        return postRepository.save(post);
    }

    public String generateSlug(String title) {
        String slug = title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        if (postRepository.existsBySlug(slug)) {
            int counter = 1;
            String baseSlug = slug;
            while (postRepository.existsBySlug(slug)) {
                slug = baseSlug + "-" + counter++;
            }
        }

        return slug;
    }
}