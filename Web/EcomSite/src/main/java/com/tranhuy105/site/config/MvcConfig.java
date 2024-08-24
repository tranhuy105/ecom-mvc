package com.tranhuy105.site.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        addResourceHandler(registry, "user-avatars", "/user-avatars/**");
        addResourceHandler(registry, "category-images", "/category-images/**");
        addResourceHandler(registry, "product-images", "/product-images/**");
    }

    private void addResourceHandler(ResourceHandlerRegistry registry, String directory, String urlPattern) {
        Path dirPath = Paths.get(directory);
        String absolutePath = dirPath.toFile().getAbsolutePath();

        registry.addResourceHandler(urlPattern)
                .addResourceLocations("file:" + absolutePath + "/");
    }
}
