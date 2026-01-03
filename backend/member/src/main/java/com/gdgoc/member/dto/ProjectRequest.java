package com.gdgoc.member.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ProjectRequest(
        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 50, message = "제목은 50자 이내여야 합니다.")
        String title,

        @Size(max = 100, message = "한 줄 소개는 100자 이내여야 합니다.")
        String description,

        String externalUrl,

        @NotBlank(message = "내용은 필수입니다.")
        String content,

        @Valid
        List<RecruitmentRequest> recruitments,

        LocalDate deadline

){
}
