package com.gdgoc.member.domain.profile.dto.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gdgoc.member.domain.profile.type.Part;
import com.gdgoc.member.domain.profile.type.Role;


public class ProfileSummaryResponse {

    @JsonProperty("user_id")
    private final UUID userId;

    private final String name;
    private final Integer generation;
    private final Part part;
    private final Role role;
    private final String department;
    private final String bio;

    @JsonProperty("profile_image_url")
    private final String profileImageUrl;

    public ProfileSummaryResponse(UUID userId, String name, Integer generation, Part part, Role role,
                                  String department, String bio, String profileImageUrl) {
        this.userId = userId;
        this.name = name;
        this.generation = generation;
        this.part = part;
        this.role = role;
        this.department = department;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
    }

    public UUID getUserId() { return userId; }
    public String getName() { return name; }
    public Integer getGeneration() { return generation; }
    public Part getPart() { return part; }
    public Role getRole() { return role; }
    public String getDepartment() { return department; }
    public String getBio() { return bio; }
    public String getProfileImageUrl() { return profileImageUrl; }
}
