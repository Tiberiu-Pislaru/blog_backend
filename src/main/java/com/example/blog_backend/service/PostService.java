package com.example.blog_backend.service;

import com.example.blog_backend.dto.PostDTO;
import com.example.blog_backend.dto.TagDTO;
import com.example.blog_backend.entity.Post;
import com.example.blog_backend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final CategoryService categoryService;

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

    public Page<PostDTO> findPublishedPosts(Pageable pageable) {
        return postRepository.findPublishedPosts(pageable).map(PostDTO::convertToDTO);
    }
    public List<PostDTO> findAllPosts() {
        return postRepository.findAll().stream()
                .map(PostDTO::convertToDTO)
                .toList();
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

    public Page<PostDTO> searchPosts(String keyword, Long categoryId, Pageable pageable) {
        return postRepository.findPublishedPostsByKeyword(keyword, categoryId, pageable).map(PostDTO::convertToDTO);
    }

    public Page<Post> findMostViewedPosts(Pageable pageable) {
        return postRepository.findMostViewedPosts(pageable);
    }

    public Post updatePost(Long id, PostDTO postDetails) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setTitle(postDetails.getTitle());
        post.setSlug(postDetails.getSlug());
        post.setExcerpt(postDetails.getExcerpt());
        post.setContent(postDetails.getContent());
        post.setFeaturedImageUrl(postDetails.getFeaturedImageUrl());
        post.setStatus(postDetails.getStatus());
        post.setCategory(categoryService.findById(postDetails.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + postDetails.getCategoryId())));
        if (Objects.nonNull(postDetails.getTags())) post.setTags(postDetails.getTags().stream().map(TagDTO::toEntity).toList());
        if (postDetails.getStatus() == Post.Status.PUBLISHED && post.getPublishedAt() == null) post.setPublishedAt(LocalDateTime.now());
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

    /**
     * Ottiene i top N articoli in evidenza
     */
    public List<PostDTO> getTopFeaturedArticles(int limit) {
        List<Post> topPosts = postRepository.findTopFeaturedPosts(limit);

        return topPosts.stream()
                .map(PostDTO::convertToDTO).toList();
    }
}