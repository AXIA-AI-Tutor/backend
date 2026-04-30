package com.ax.avatarcoach.domain.user.repository;

import com.ax.avatarcoach.domain.user.entity.OAuthProvider;
import com.ax.avatarcoach.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Google 로그인 때 사용
     */
    Optional<User> findByProviderAndProviderUserId(
        OAuthProvider provider,
        String providerUserId
    );
}