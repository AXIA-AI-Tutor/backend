package com.ax.avatarcoach.domain.user.dto;

import com.ax.avatarcoach.domain.user.entity.Role;
import com.ax.avatarcoach.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "현재 로그인 사용자 응답")
public record UserMeResponse(
    @Schema(description = "사용자 ID", example = "1")
    Long id,

    @Schema(description = "이메일", example = "user@example.com")
    String email,

    @Schema(description = "닉네임", example = "홍길동")
    String nickname,

    @Schema(description = "프로필 이미지 URL", example = "https://lh3.googleusercontent.com/a/example")
    String profileImageUrl,

    @Schema(description = "사용자 권한", example = "USER")
    Role role
) {

    public static UserMeResponse from(User user) {
        return new UserMeResponse(
            user.getId(),
            user.getEmail(),
            user.getNickname(),
            user.getProfileImageUrl(),
            user.getRole()
        );
    }
}
