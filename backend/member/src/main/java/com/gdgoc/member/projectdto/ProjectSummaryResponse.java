package com.gdgoc.member.projectdto;

import com.gdgoc.member.domain.Project;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ProjectSummaryResponse(
        UUID projectId,
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
