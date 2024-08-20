package com.tranhuy105.admin.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@EntityScan({"com.tranhuy105.common.entity", "com.tranhuy105.admin.user"})
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path avatarDir = Paths.get("user-avatars");
        String userAvatarPath = avatarDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/user-avatars/**")
                .addResourceLocations("file:/"+userAvatarPath+"/");
    }
}
