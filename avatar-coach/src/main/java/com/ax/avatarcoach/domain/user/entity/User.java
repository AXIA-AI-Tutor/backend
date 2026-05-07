package com.ax.avatarcoach.domain.user.entity;

import com.ax.avatarcoach.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(name = "uk_users_provider_provider_user_id", columnNames = {"provider", "provider_user_id"})})
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String email;

    @Column(length = 50)
    private String nickname;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OAuthProvider provider;

    @Column(name = "provider_user_id", nullable = false, length = 100)
    private String providerUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;


    public static User createOAuthUser(
        String email,
        String nickname,
        String profileImageUrl,
        OAuthProvider provider,
        String providerUserId
    ) {
        User user = new User();
        user.email = email;
        user.nickname = nickname;
        user.profileImageUrl = profileImageUrl;
        user.provider = provider;
        user.providerUserId = providerUserId;
        user.role = Role.USER;
        return user;
    }
}
