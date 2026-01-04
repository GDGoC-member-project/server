package com.gdgoc.member.projectdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RecruitmentRequest(
        @NotBlank(message = "파트 이름은 필수 입니다.")
        @Size(max = 50)
        String position,

        @NotBlank(message = "담당 업무 입력은 필수 입니다.")
        String description,

        @Min(0)
        Integer filled,

        @NotNull(message = "최대 모집 인원은 필수 입니다.")
        @Min(0)
        Integer max
) {
}
