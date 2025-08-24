package com.example.blog_backend.dto;

import com.example.blog_backend.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
    private String firstName;
    private String lastName;
    private String bio;
    private String profileImageUrl;
    private User.Role role;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;

    public UserDTO(Long id, String username, String email, String password, String firstName, String lastName, String bio, String profileImageUrl, Boolean isActive, User.Role role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
        this.isActive = isActive;
        this.role = role;
        this.createdAt = createdAt.toString();
        this.updatedAt = updatedAt.toString();
    }

    public User toEntity() {
        User user = new User();
        user.setId(this.id);
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        user.setBio(this.bio);
        user.setProfileImageUrl(this.profileImageUrl);
        user.setRole(this.role != null ? this.role : User.Role.USER);
        user.setIsActive( this.isActive );
        return user;
    }

    public static UserDTO fromEntity(User user) {
        return new UserDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getPassword(),
            user.getFirstName(),
            user.getLastName(),
            user.getBio(),
            user.getProfileImageUrl(),
            user.getIsActive(),
            user.getRole(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}