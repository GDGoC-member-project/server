package com.gdgoc.member.domain.profile.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gdgoc.member.domain.profile.type.Part;
import com.gdgoc.member.domain.profile.type.Role;
import com.gdgoc.member.domain.profile.type.SocialLink;

import java.util.List;

public class ProfileResponse {

    @JsonProperty("user_id")
    private final String userId;

    private final String name;
    private final Integer generation;
    private final Part part;
    private final Role role;
    private final String department;
    private final String bio;

    @JsonProperty("mbti_info")
    private final String mbtiInfo;

    @JsonProperty("profile_image_url")
    private final String profileImageUrl;

    @JsonProperty("tech_stacks")
    private final List<String> techStacks;

    @JsonProperty("social_links")
    private final List<SocialLink> socialLinks;

    public ProfileResponse(String userId, String name, Integer generation, Part part, Role role,
                           String department, String bio, String mbtiInfo, String profileImageUrl,
                           List<String> techStacks, List<SocialLink> socialLinks) {
        this.userId = userId;
        this.name = name;
        this.generation = generation;
        this.part = part;
        this.role = role;
        this.department = department;
        this.bio = bio;
        this.mbtiInfo = mbtiInfo;
        this.profileImageUrl = profileImageUrl;
        this.techStacks = techStacks;
        this.socialLinks = socialLinks;
    }

    public String getUserId() { return userId.toString(); }
    public String getName() { return name; }
    public Integer getGeneration() { return generation; }
    public Part getPart() { return part; }
    public Role getRole() { return role; }
    public String getDepartment() { return department; }
    public String getBio() { return bio; }
    public String getMbtiInfo() { return mbtiInfo; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public List<String> getTechStacks() { return techStacks; }
    public List<SocialLink> getSocialLinks() { return socialLinks; }
}
