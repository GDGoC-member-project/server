package com.gdgoc.member.dto;

import com.gdgoc.member.domain.Project;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ProjectSummaryResponse(
        String projectId,
        String title,
        String description,
        List<RrecruitmentSummaryResponse> recruitments,
        LocalDate deadline

) {
    public ProjectSummaryResponse(Project project) {
        this(
                project.getProjectId(),
                project.getTitle(),
                project.getDescription(),
                project.getRecruitments().stream()
                        .map(RrecruitmentSummaryResponse::new)
                        .toList(),
                project.getDeadline()

        );
    }


}
