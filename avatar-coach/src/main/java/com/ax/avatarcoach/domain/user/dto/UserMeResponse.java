package com.ax.avatarcoach.domain.user.dto;

import com.ax.avatarcoach.domain.user.entity.Role;
import com.ax.avatarcoach.domain.user.entity.User;

public record UserMeResponse(
    Long id,
    String email,
    String nickname,
    String profileImageUrl,
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
