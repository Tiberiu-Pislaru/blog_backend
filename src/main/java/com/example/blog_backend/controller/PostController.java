package com.example.blog_backend.controller;

import com.example.blog_backend.dto.ApiResponseDTO;
import com.example.blog_backend.dto.PagedResponseDTO;
import com.example.blog_backend.dto.PostDTO;
import com.example.blog_backend.dto.TagDTO;
import com.example.blog_backend.entity.Category;
import com.example.blog_backend.entity.Post;
import com.example.blog_backend.entity.User;
import com.example.blog_backend.service.CategoryService;
import com.example.blog_backend.service.PostService;
import com.example.blog_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final CategoryService categoryService;

    @GetMapping
    public PagedResponseDTO<PostDTO> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "publishedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PostDTO> posts = postService.findPublishedPosts(pageable);
        // Convert posts to PostDTO if needed
        PagedResponseDTO<PostDTO> pagedResponseDTO = new PagedResponseDTO<>(posts);
        return pagedResponseDTO;
    }

    @GetMapping("/all")
    public ApiResponseDTO<List<PostDTO>> getAllPostsAll() {

        List<PostDTO> posts = postService.findAllPosts();
        // Convert posts to PostDTO if needed
        // ;
        return ApiResponseDTO.success("All posts retrieved successfully.", posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Optional<Post> post = postService.findById(id);
        if (post.isPresent()) {
            postService.incrementViewCount(id);
            return ResponseEntity.ok(post.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/slug/{slug}")
    public ApiResponseDTO<PostDTO> getPostBySlug(@PathVariable String slug) {
        Optional<Post> post = postService.findBySlug(slug);
        if (post.isPresent()) {
            postService.incrementViewCount(post.get().getId());
            return ApiResponseDTO.success("Post retrieved successfully.", PostDTO.convertToDTO(post.get()));
        }
        return ApiResponseDTO.error("Post not found with slug: " + slug);
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<Page<Post>> getPostsByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postService.findPostsByAuthor(authorId, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<Post>> getPostsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postService.findPostsByCategory(categoryId, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/tag/{tagId}")
    public ResponseEntity<Page<Post>> getPostsByTag(
            @PathVariable Long tagId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postService.findPostsByTag(tagId, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/search")
    public PagedResponseDTO<PostDTO> searchPosts(
            @RequestParam (required = false) String keyword,
            @RequestParam Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PostDTO> posts = postService.searchPosts(keyword, categoryId, pageable);
        return new PagedResponseDTO<>(posts);
    }

    @GetMapping("/popular")
    public ResponseEntity<Page<Post>> getPopularPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postService.findMostViewedPosts(pageable);
        return ResponseEntity.ok(posts);
    }

    @PostMapping
    public ApiResponseDTO<PostDTO> createPost(@Valid @RequestBody PostDTO post) {
        try {
            Post newPost = new Post();
            if (post.getSlug() == null || post.getSlug().isEmpty()) {
                post.setSlug(postService.generateSlug(post.getTitle()));
            }

            Optional<User> author = userService.findById(post.getAuthorId());
            if (author.isEmpty()) {
                return ApiResponseDTO.error("Author not found with ID: " + post.getAuthorId());
            }
            newPost.setAuthor(author.get());


            Optional<Category> category = categoryService.findById(post.getCategoryId());
            if (category.isEmpty()) {
                return ApiResponseDTO.error("Category not found with ID: " + post.getCategoryId());
            }
            newPost.setCategory(category.get());

        // Convert PostDTO to Post entity
            newPost.setTitle(post.getTitle());
            newPost.setSlug(post.getSlug());
            newPost.setExcerpt(post.getExcerpt());
            newPost.setContent(post.getContent());
            newPost.setFeaturedImageUrl(post.getFeaturedImageUrl());
            newPost.setStatus(post.getStatus() != null ? post.getStatus() : Post.Status.DRAFT);

            newPost.setTags(post.getTags() != null ? post.getTags().stream().map(TagDTO::toEntity).toList() : null);
            newPost.setViewCount(0L);
            newPost.setFeatured(true);
            newPost.setPublishedAt(post.getStatus() == Post.Status.PUBLISHED ? LocalDateTime.now() : null);
            Post createdPost = postService.createPost(newPost);
            PostDTO createdPostDTO = new PostDTO().fromEntity(createdPost);
            return ApiResponseDTO.success("Entity created.",createdPostDTO);
        } catch (RuntimeException e) {
            return ApiResponseDTO.error("Failed to create post: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponseDTO<PostDTO> updatePost(@PathVariable Long id, @Valid @RequestBody PostDTO postDetails) {
        try {
            Post updatedPost = postService.updatePost(id, postDetails);
            return ApiResponseDTO.success("Post updated successfully.", new PostDTO().fromEntity(updatedPost));
        } catch (RuntimeException e) {
            return ApiResponseDTO.error("Failed to update post: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/v1/articles/featured/top/{limit}
     * Ottiene i top N articoli in evidenza
     */
    @GetMapping("/featured/top/{limit}")
    public ApiResponseDTO<List<PostDTO>> getTopFeaturedArticles(
            @PathVariable int limit) {

        List<PostDTO> posts = postService.getTopFeaturedArticles(limit);
        return ApiResponseDTO.success("Top featured articles retrieved successfully.", posts);
    }
}