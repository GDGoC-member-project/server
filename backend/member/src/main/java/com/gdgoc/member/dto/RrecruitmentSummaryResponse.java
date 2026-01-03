package com.gdgoc.member.dto;

import com.gdgoc.member.domain.Recruitment;

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
