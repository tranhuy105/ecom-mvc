package com.tranhuy105.admin.security;

import com.tranhuy105.common.entity.Role;
import com.tranhuy105.common.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class MyUserDetail implements UserDetails {
    private User user;

    public MyUserDetail(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<Role> roles = user.getRoles();
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.getName())).toList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    public String getFullName(){
        return user.getFirstName() + " " + user.getLastName();
    }

    public String getAvatarPath() {
        return user.getAvatarPath();
    }
}
