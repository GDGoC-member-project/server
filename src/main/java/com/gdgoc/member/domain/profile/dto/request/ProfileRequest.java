package com.gdgoc.member.domain.profile.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gdgoc.member.domain.profile.type.Part;
import com.gdgoc.member.domain.profile.type.Role;
import com.gdgoc.member.domain.profile.type.SocialLink;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class ProfileRequest {

    @NotBlank(message = "name은 필수입니다.")
    private String name;

    private Integer generation;
    private Part part;
    private Role role;
    private String department;
    private String bio;

    @JsonProperty("mbti_info")
    private String mbtiInfo;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;

    @JsonProperty("tech_stacks")
    private List<String> techStacks;

    @JsonProperty("social_links")
    private List<SocialLink> socialLinks;

    public ProfileRequest() {}

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

    public void setName(String name) { this.name = name; }
    public void setGeneration(Integer generation) { this.generation = generation; }
    public void setPart(Part part) { this.part = part; }
    public void setRole(Role role) { this.role = role; }
    public void setDepartment(String department) { this.department = department; }
    public void setBio(String bio) { this.bio = bio; }
    public void setMbtiInfo(String mbtiInfo) { this.mbtiInfo = mbtiInfo; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public void setTechStacks(List<String> techStacks) { this.techStacks = techStacks; }
    public void setSocialLinks(List<SocialLink> socialLinks) { this.socialLinks = socialLinks; }
}
