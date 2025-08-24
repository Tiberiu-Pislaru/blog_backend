package com.example.blog_backend.dto;

import com.example.blog_backend.entity.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagDTO {
    private Long id;

    @NotBlank(message = "Tag name is required")
    @Size(max = 50, message = "Tag name must not exceed 50 characters")
    private String name;

    @NotBlank(message = "Tag slug is required")
    @Size(max = 50, message = "Tag slug must not exceed 50 characters")
    private String slug;

    private String createdAt;
    private String updatedAt;

    public Tag toEntity() {
        Tag tag = new Tag();
        tag.setId(this.id);
        tag.setName(this.name);
        tag.setSlug(this.slug);
        return tag;
    }

    public TagDTO fromEntity(Tag tag) {
        this.id = tag.getId();
        this.name = tag.getName();
        this.slug = tag.getSlug();
        this.createdAt = tag.getCreatedAt().toString();
        this.updatedAt = tag.getUpdatedAt().toString();
        return this;
    }
}