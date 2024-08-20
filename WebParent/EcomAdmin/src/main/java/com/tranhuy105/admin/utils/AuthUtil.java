package com.tranhuy105.admin.utils;

import com.tranhuy105.admin.security.MyUserDetail;
import com.tranhuy105.common.entity.User;
import org.springframework.security.core.Authentication;

public class AuthUtil {
    public static User extractUserFromAuthentication(Authentication authentication) {
        return ((MyUserDetail) authentication.getPrincipal()).getUser();
    }
}
