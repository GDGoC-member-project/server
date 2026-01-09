package com.gdgoc.member.projectdto;

import com.gdgoc.member.domain.project.Recruitment;

public record RecruitmentResponse(
    String position,
    String description,
    Integer filled,
    Integer max
            ) {
    public RecruitmentResponse (Recruitment recruitment) {
        this(
                recruitment.getPosition(),
                recruitment.getDescription(),
                recruitment.getFilled(),
                recruitment.getMax()
        );
    }
}
