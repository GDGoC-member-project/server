package com.gdgoc.member.projectdto;

import com.gdgoc.member.domain.Project;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public record ProjectResponse(
        UUID projectId,
        String title,
        String description,
        String externalUrl,
        String content,
        List<com.gdgoc.member.projectdto.RecruitmentResponse> recruitments,
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
