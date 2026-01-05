package com.gdgoc.member.domain.project;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Embeddable
@Table(name = "project_recruitments")

public class Recruitment {

    @Column(name = "position", nullable = false)
    private String position;      // 모집 파트(optional)

    @Column(name = "recruitment_description", nullable = false)
    private String description;   // 파트 담당 업무 설명 (optional)

    @Column(name = "filled")
    private Integer filled;       // 현재 지원자 수 (optional)

    @Column(name = "max_count", nullable = false)
    private Integer max;          // 최대 모집 인원 (optional)

    public Recruitment(String position, String description, Integer filled, Integer max) {
        this.position = position;
        this.description = description;
        this.filled = filled;
        this.max = max;
    }
}
