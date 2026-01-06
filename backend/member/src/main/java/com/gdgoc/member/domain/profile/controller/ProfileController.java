package com.gdgoc.member.domain.profile.controller;

import com.gdgoc.member.domain.profile.dto.request.ProfileRequest;
import com.gdgoc.member.domain.profile.dto.response.ProfileImageUploadResponse;
import com.gdgoc.member.domain.profile.dto.response.ProfileResponse;
import com.gdgoc.member.domain.profile.dto.response.ProfileSummaryResponse;
import com.gdgoc.member.domain.profile.service.ProfileService;
import com.gdgoc.member.domain.profile.type.Part;
import com.gdgoc.member.domain.profile.type.Role;
import com.gdgoc.member.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/me")
    public BaseResponse<ProfileResponse> create(
        @Valid @RequestBody ProfileRequest req
    ) {
        return BaseResponse.success(profileService.create(req));
    }

    @GetMapping("/me")
    public BaseResponse<ProfileResponse> getMe() {
        return BaseResponse.success(profileService.get());
    }

    @PatchMapping("/me")
    public BaseResponse<ProfileResponse> updateMe(
        @Valid @RequestBody ProfileRequest req
    ) {
        return BaseResponse.success(profileService.update(req));
    }

    @GetMapping
    public BaseResponse<List<ProfileSummaryResponse>> list() {
        return BaseResponse.success(profileService.list());
    }

    @GetMapping("/filter")
    public BaseResponse<List<ProfileSummaryResponse>> filter(
        @RequestParam Part part,
        @RequestParam Role role
    ) {
        return BaseResponse.success(profileService.filter(part, role));
    }

    @GetMapping("/search")
    public BaseResponse<List<ProfileSummaryResponse>> search(@RequestParam String keyword) {
        return BaseResponse.success(profileService.search(keyword));
    }

    @GetMapping("/{userId}")
    public BaseResponse<ProfileResponse> getProfileDetail(@PathVariable UUID userId) {
        return BaseResponse.success(profileService.getByUserId(userId));
    }

    @PostMapping(value = "/upload_image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로필 이미지 업로드", description = "프로필 이미지를 S3에 업로드하고 DB에 저장합니다.")
    public BaseResponse<ProfileImageUploadResponse> uploadProfileImage(
            @Parameter(description = "업로드할 이미지 파일 (이미지 파일만 가능, 최대 10MB)", required = true)
            @RequestParam("file") MultipartFile file
    ) {
        return BaseResponse.success(profileService.uploadProfileImage(file));
    }

}
