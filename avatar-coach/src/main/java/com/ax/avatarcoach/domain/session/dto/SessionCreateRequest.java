package com.ax.avatarcoach.domain.session.dto;

import com.ax.avatarcoach.domain.session.entity.SessionDifficulty;
import com.ax.avatarcoach.domain.session.entity.SessionMode;
import com.ax.avatarcoach.domain.session.entity.SessionTarget;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "세션 생성 요청")
public record SessionCreateRequest(
    @Schema(description = "연습 모드", example = "INTERVIEW")
    @NotNull(message = "연습 모드는 필수입니다.")
    SessionMode mode,

    @Schema(description = "연습 대상 직무", example = "FRONTEND")
    @NotNull(message = "연습 대상은 필수입니다.")
    SessionTarget target,

    @Schema(description = "연습 난이도", example = "EASY")
    @NotNull(message = "난이도는 필수입니다.")
    SessionDifficulty difficulty
) {
}
