package com.gdgoc.member.domain.profile.entity;

import com.gdgoc.member.domain.profile.type.Part;
import com.gdgoc.member.domain.profile.type.Role;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
    name = "profiles",
    indexes = {
        @Index(name = "idx_profiles_name", columnList = "name"),
        @Index(name = "idx_profiles_part_role", columnList = "part, role")
    }
)
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true, columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID userId;

    @Column(nullable = false, length = 50)
    private String name;

    private Integer generation;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private Part part;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private Role role;

    @Column(length = 100)
    private String department;

    @Column(length = 500)
    private String bio;

    @Column(name = "mbti_info", length = 10)
    private String mbtiInfo;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "tech_stacks", columnDefinition = "json")
    private String techStacksJson;

    @Column(name = "social_links", columnDefinition = "json")
    private String socialLinksJson;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Profile() {}

    public Profile(UUID userId, String name, Integer generation, Part part, Role role,
                   String department, String bio, String mbtiInfo, String profileImageUrl,
                   String techStacksJson, String socialLinksJson) {
        this.userId = userId;
        this.name = name;
        this.generation = generation;
        this.part = part;
        this.role = role;
        this.department = department;
        this.bio = bio;
        this.mbtiInfo = mbtiInfo;
        this.profileImageUrl = profileImageUrl;
        this.techStacksJson = techStacksJson;
        this.socialLinksJson = socialLinksJson;
    }

    public void update(String name, Integer generation, Part part, Role role,
                       String department, String bio, String mbtiInfo, String profileImageUrl,
                       String techStacksJson, String socialLinksJson) {
        this.name = name;
        this.generation = generation;
        this.part = part;
        this.role = role;
        this.department = department;
        this.bio = bio;
        this.mbtiInfo = mbtiInfo;
        this.profileImageUrl = profileImageUrl;
        this.techStacksJson = techStacksJson;
        this.socialLinksJson = socialLinksJson;
    }

    public Long getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getName() { return name; }
    public Integer getGeneration() { return generation; }
    public Part getPart() { return part; }
    public Role getRole() { return role; }
    public String getDepartment() { return department; }
    public String getBio() { return bio; }
    public String getMbtiInfo() { return mbtiInfo; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public String getTechStacksJson() { return techStacksJson; }
    public String getSocialLinksJson() { return socialLinksJson; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
