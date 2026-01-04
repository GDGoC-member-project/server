package com.gdgoc.member.domain.profile.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdgoc.member.domain.profile.dto.request.ProfileRequest;
import com.gdgoc.member.domain.profile.dto.response.ProfileResponse;
import com.gdgoc.member.domain.profile.dto.response.ProfileSummaryResponse;
import com.gdgoc.member.domain.profile.entity.Profile;
import com.gdgoc.member.security.*;
import com.gdgoc.member.domain.profile.repository.ProfileRepository;
import com.gdgoc.member.domain.profile.type.Part;
import com.gdgoc.member.domain.profile.type.Role;
import com.gdgoc.member.domain.profile.type.SocialLink;
import com.gdgoc.member.global.error.ApiException;
import com.gdgoc.member.global.error.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;



@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final CurrentUserService currentUserService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProfileService(ProfileRepository profileRepository, CurrentUserService currentUserService) {
        this.profileRepository = profileRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public ProfileResponse create(ProfileRequest req) {
        CurrentUser user = currentUserService.requireUser();
        String userId = user.userId();

        if (profileRepository.existsByUserId(userId)) {
            throw new ApiException(ErrorCode.DUPLICATE_USER_ID);
        }

        String techStacksJson = toJson(req.getTechStacks());
        String socialLinksJson = toJson(req.getSocialLinks());

        Profile profile = new Profile(
            userId,
            req.getName(),
            req.getGeneration(),
            req.getPart(),
            req.getRole(),
            req.getDepartment(),
            req.getBio(),
            req.getMbtiInfo(),
            req.getProfileImageUrl(),
            techStacksJson,
            socialLinksJson
        );

        Profile saved = profileRepository.save(profile);
        return toProfileResponse(saved);
    }

    @Transactional(readOnly = true)
    public ProfileResponse get() {
        String userId = currentUserService.requireUser().userId();

        Profile profile = profileRepository.findByUserId(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.PROFILE_NOT_FOUND));

        return toProfileResponse(profile);
    }

    @Transactional
    public ProfileResponse update(ProfileRequest req) {
        String userId = currentUserService.requireUser().userId();

        Profile profile = profileRepository.findByUserId(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.PROFILE_NOT_FOUND));

        profile.update(
            req.getName(),
            req.getGeneration(),
            req.getPart(),
            req.getRole(),
            req.getDepartment(),
            req.getBio(),
            req.getMbtiInfo(),
            req.getProfileImageUrl(),
            toJson(req.getTechStacks()),
            toJson(req.getSocialLinks())
        );

        return toProfileResponse(profile);
    }

    @Transactional(readOnly = true)
    public List<ProfileSummaryResponse> list() {
        return profileRepository.findAll().stream()
            .map(this::toSummaryResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<ProfileSummaryResponse> filter(Part part, Role role) {
        return profileRepository.findByPartAndRole(part, role).stream()
            .map(this::toSummaryResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<ProfileSummaryResponse> search(String keyword) {
        return profileRepository.findByNameContainingIgnoreCase(keyword).stream()
            .map(this::toSummaryResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public ProfileResponse getByUserId(String userId) {
        Profile profile = profileRepository.findByUserId(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.PROFILE_NOT_FOUND));

        return toProfileResponse(profile);
    }


    private ProfileSummaryResponse toSummaryResponse(Profile p) {
        return new ProfileSummaryResponse(
            p.getUserId(),
            p.getName(),
            p.getGeneration(),
            p.getPart(),
            p.getRole(),
            p.getDepartment(),
            p.getBio(),
            p.getProfileImageUrl()
        );
    }

    private ProfileResponse toProfileResponse(Profile p) {
        return new ProfileResponse(
            p.getUserId(),
            p.getName(),
            p.getGeneration(),
            p.getPart(),
            p.getRole(),
            p.getDepartment(),
            p.getBio(),
            p.getMbtiInfo(),
            p.getProfileImageUrl(),
            fromJsonList(p.getTechStacksJson(), new TypeReference<List<String>>() {}),
            fromJsonList(p.getSocialLinksJson(), new TypeReference<List<SocialLink>>() {})
        );
    }

    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "JSON 직렬화 실패");
        }
    }

    private <T> List<T> fromJsonList(String json, TypeReference<List<T>> typeRef) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
