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
import java.util.UUID;


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
        
        UUID userId = user.userId();
        
        // 디버깅: 저장하는 userId 로그 출력
        System.out.println("DEBUG: 저장하는 userId = " + userId);

        // 이미 존재하면 조회해서 반환 (getOrCreate 패턴)
        Profile existingProfile = profileRepository.findByUserId(userId).orElse(null);
        if (existingProfile != null) {
            System.out.println("DEBUG: 기존 프로필 발견. userId = " + userId);
            return toProfileResponse(existingProfile);
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

        try {
            Profile saved = profileRepository.save(profile);
            System.out.println("DEBUG: 프로필 저장 성공. userId = " + saved.getUserId());
            return toProfileResponse(saved);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // 동시성 문제로 인한 중복 저장 시도 시, 기존 프로필 조회
            System.out.println("DEBUG: 중복 저장 시도. 기존 프로필 조회. userId = " + userId);
            Profile savedProfile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.DUPLICATE_USER_ID));
            System.out.println("DEBUG: 기존 프로필 조회 성공. userId = " + savedProfile.getUserId());
            return toProfileResponse(savedProfile);
        }
    }

    @Transactional(readOnly = true)
    public ProfileResponse get() {
        CurrentUser user = currentUserService.requireUser();
        UUID userId = user.userId();

        // 디버깅: 조회하는 userId 로그 출력
        System.out.println("DEBUG: 조회하는 userId = " + userId);
        
        Profile profile = profileRepository.findByUserId(userId)
            .orElseThrow(() -> {
                // 디버깅: 프로필이 없을 때 모든 프로필의 userId 확인
                System.out.println("DEBUG: 프로필을 찾을 수 없음. userId = " + userId);
                System.out.println("DEBUG: 전체 프로필 수 = " + profileRepository.count());
                return new ApiException(ErrorCode.PROFILE_NOT_FOUND);
            });

        return toProfileResponse(profile);
    }

    @Transactional
    public ProfileResponse update(ProfileRequest req) {
        UUID userId = currentUserService.requireUser().userId();

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
    public ProfileResponse getByUserId(UUID userId) {
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
