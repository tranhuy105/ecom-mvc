package com.tranhuy105.site.setting;

import com.tranhuy105.common.entity.Setting;
import com.tranhuy105.common.entity.SettingCategory;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SettingFilter implements Filter {
    private final SettingService settingService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest servletRequest = (HttpServletRequest) request;
        String url = servletRequest.getRequestURL().toString();

        if (url.endsWith(".css") || url.endsWith(".js") || url.endsWith(".png") || url.endsWith(".jpg")) {
            chain.doFilter(request, response);
            return;
        }

        setSettingsAttributes(request, SettingCategory.GENERAL);
        setSettingsAttributes(request, SettingCategory.CURRENCY);
        chain.doFilter(request, response);
    }

    private void setSettingsAttributes(ServletRequest request, SettingCategory category) {
        List<Setting> settings = settingService.findAll();
        settings.stream()
                .filter(setting -> setting.getCategory().equals(category))
                .forEach(setting -> request.setAttribute(setting.getKey(), setting.getValue()));
    }
}
