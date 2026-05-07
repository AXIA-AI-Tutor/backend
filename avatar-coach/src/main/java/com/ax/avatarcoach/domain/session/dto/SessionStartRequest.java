package com.ax.avatarcoach.domain.session.dto;

import com.ax.avatarcoach.domain.session.entity.SessionDifficulty;
import com.ax.avatarcoach.domain.session.entity.SessionMode;
import com.ax.avatarcoach.domain.session.entity.SessionTarget;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "세션 시작 요청")
public record SessionStartRequest(
    @Schema(description = "연습 모드", example = "INTERVIEW")
    @NotNull(message = "연습 모드는 필수입니다.")
    SessionMode mode,

    @Schema(description = "지원 직무", example = "BACKEND")
    @NotNull(message = "지원 직무는 필수입니다.")
    SessionTarget target,

    @Schema(description = "난이도", example = "NORMAL")
    @NotNull(message = "난이도는 필수입니다.")
    SessionDifficulty difficulty
) {
}
