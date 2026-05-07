package com.ax.avatarcoach.global.security.oauth;

import java.util.Map;

public class GoogleOAuth2UserInfo {

    private final Map<String, Object> attributes;

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getProviderUserId() {
        return getString("sub");
    }

    public String getEmail() {
        return getString("email");
    }

    public String getNickname() {
        return getString("name");
    }

    public String getProfileImageUrl() {
        return getString("picture");
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    private String getString(String key) {
        Object value = attributes.get(key);
        return value instanceof String stringValue ? stringValue : null;
    }
}
