package com.gdgoc.member.domain.profile.controller;

import com.gdgoc.member.domain.profile.dto.request.ProfileRequest;
import com.gdgoc.member.domain.profile.dto.response.ProfileResponse;
import com.gdgoc.member.domain.profile.dto.response.ProfileSummaryResponse;
import com.gdgoc.member.domain.profile.service.ProfileService;
import com.gdgoc.member.domain.profile.type.Part;
import com.gdgoc.member.domain.profile.type.Role;
import com.gdgoc.member.global.response.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public BaseResponse<ProfileResponse> getProfileDetail(@PathVariable String userId) {
        return BaseResponse.success(profileService.getByUserId(userId));
    }

}
