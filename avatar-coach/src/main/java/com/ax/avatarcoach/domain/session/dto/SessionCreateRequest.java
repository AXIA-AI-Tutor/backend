package com.ax.avatarcoach.domain.session.dto;

import com.ax.avatarcoach.domain.session.entity.SessionDifficulty;
import com.ax.avatarcoach.domain.session.entity.SessionMode;
import com.ax.avatarcoach.domain.session.entity.SessionTarget;
import jakarta.validation.constraints.NotNull;

public record SessionCreateRequest(
    @NotNull(message = "연습 모드는 필수입니다.")
    SessionMode mode,

    @NotNull(message = "연습 대상은 필수입니다.")
    SessionTarget target,

    @NotNull(message = "난이도는 필수입니다.")
    SessionDifficulty difficulty
) {
}
