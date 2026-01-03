package com.gdgoc.member.dto;

import com.gdgoc.member.domain.Project;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public record ProjectResponse(
        String projectId,
        String title,
        String description,
        String externalUrl,
        String content,
        List<com.gdgoc.member.dto.RecruitmentResponse> recruitments,
        LocalDate deadline,
        LocalDateTime postDate

){
    public ProjectResponse(Project project) {
        this(
                project.getProjectId(),
                project.getTitle(),
                project.getDescription(),
                project.getExternalUrl(),
                project.getContent(),
                project.getRecruitments().stream()
                        .map(RecruitmentResponse::new)
                        .toList(),
                project.getDeadline(),
                project.getPostDate()
        );
    }


}
