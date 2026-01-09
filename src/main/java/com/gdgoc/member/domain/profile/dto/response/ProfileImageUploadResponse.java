package com.gdgoc.member.domain.profile.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class ProfileImageUploadResponse {

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;

    @JsonProperty("file_size")
    private Long fileSize;

    @JsonProperty("file_name")
    private String fileName;

    public ProfileImageUploadResponse() {}

    public ProfileImageUploadResponse(UUID userId, String profileImageUrl, Long fileSize, String fileName) {
        this.userId = userId;
        this.profileImageUrl = profileImageUrl;
        this.fileSize = fileSize;
        this.fileName = fileName;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

