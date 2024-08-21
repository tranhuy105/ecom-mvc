package com.tranhuy105.admin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path avatarDir = Paths.get("user-avatars");
        String userAvatarPath = avatarDir.toFile().getAbsolutePath();

        Path categoryImageDir = Paths.get("category-images");
        String categoryImagePath = categoryImageDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/user-avatars/**")
                .addResourceLocations("file:/"+userAvatarPath+"/");

        registry.addResourceHandler("/category-images/**")
                .addResourceLocations("file:/"+categoryImagePath+"/");
    }
}
