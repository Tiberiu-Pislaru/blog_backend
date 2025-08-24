package com.example.blog_backend.repository;

import com.example.blog_backend.dto.UserDTO;
import com.example.blog_backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT new com.example.blog_backend.dto.UserDTO( u.id, u.username, u.email, null, u.firstName, u.lastName, u.bio, u.profileImageUrl, u.isActive, u.role, " +
            "u.createdAt, u.updatedAt) " +
            "FROM User u WHERE u.isActive = true")
    Page<UserDTO> findActiveUsers(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true")
    Page<User> findByRoleAndActive(@Param("role") User.Role role, Pageable pageable);
}