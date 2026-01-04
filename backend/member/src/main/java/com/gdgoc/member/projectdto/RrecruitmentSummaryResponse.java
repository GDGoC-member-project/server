package com.gdgoc.member.projectdto;

import com.gdgoc.member.domain.project.Recruitment;

public record RrecruitmentSummaryResponse (
        String position,
        Integer filled,
        Integer max
){
    public RrecruitmentSummaryResponse(Recruitment recruitment) {
        this(
                recruitment.getPosition(),
                recruitment.getFilled(),
                recruitment.getMax()
        );
    }
}
