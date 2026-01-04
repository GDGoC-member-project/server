package com.gdgoc.member.projectdto;

import com.gdgoc.member.domain.Recruitment;

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
