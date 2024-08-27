package com.tranhuy105.site.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomerOAuth2User implements OAuth2User {
    private final OAuth2User oAuth2User;
    private String fullName;

    public CustomerOAuth2User(OAuth2User oAuth2User) {
        this.oAuth2User = oAuth2User;
        this.fullName = getName();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return oAuth2User.getAttribute("name");
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePicture() {
        return oAuth2User.getAttribute("picture");
    }

    public String getEmail() {
        return oAuth2User.getAttribute("email");
    }

    public String getFirstName() {
        return oAuth2User.getAttribute("given_name");
    }

    public String getLastName() {
        return oAuth2User.getAttribute("family_name");
    }
}
