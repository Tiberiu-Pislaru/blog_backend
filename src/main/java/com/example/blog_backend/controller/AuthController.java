package com.example.blog_backend.controller;

import com.example.blog_backend.dto.*;
import com.example.blog_backend.entity.User;
import com.example.blog_backend.security.JwtTokenProvider;
import com.example.blog_backend.security.UserPrincipal;
import com.example.blog_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userService.findById(userPrincipal.getId()).orElseThrow();

        UserDTO userDTO = convertToUserDTO(user);
        AuthResponseDTO authResponse = new AuthResponseDTO(jwt, userDTO);

        return ResponseEntity.ok(ApiResponseDTO.success("Login successful", authResponse));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<UserDTO>> registerUser(@Valid @RequestBody RegisterRequestDTO signUpRequest) {
        try {
            User user = new User();
            user.setUsername(signUpRequest.getUsername());
            user.setEmail(signUpRequest.getEmail());
            user.setPassword(signUpRequest.getPassword());
            user.setFirstName(signUpRequest.getFirstName());
            user.setLastName(signUpRequest.getLastName());
            user.setRole(User.Role.USER);

            User createdUser = userService.createUser(user);
            UserDTO userDTO = convertToUserDTO(createdUser);

            return ResponseEntity.ok(ApiResponseDTO.success("User registered successfully", userDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponseDTO<UserDTO>> getCurrentUser() {
        // Extract user from JWT token and return user details
        Authentication user2 = SecurityContextHolder.getContext().getAuthentication();

        User user = userService.findById(((UserPrincipal) user2.getPrincipal()).getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDTO userDTO = convertToUserDTO(user);

        return ResponseEntity.ok(ApiResponseDTO.success(userDTO));
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setBio(user.getBio());
        userDTO.setProfileImageUrl(user.getProfileImageUrl());
        userDTO.setRole(user.getRole());
        userDTO.setIsActive(user.getIsActive());
        userDTO.setCreatedAt(user.getCreatedAt().toString());
        userDTO.setUpdatedAt(user.getUpdatedAt().toString());
        return userDTO;
    }
}