package com.tranhuy105.admin.controller;

import com.tranhuy105.admin.service.SettingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class SettingController {
    private final SettingService settingService;

    @GetMapping("/settings")
    public String settingListingView(Model model) {
        model.addAttribute("currencyList", settingService.findAllCurrency());
        settingService.findAll().forEach(setting -> {
            model.addAttribute(setting.getKey(), setting.getValue());
        });

        return "settings/setting_form";
    }

    @PostMapping("/settings")
    public String saveSetting(RedirectAttributes redirectAttributes, HttpServletRequest request) {
        try {
            settingService.saveSetting(request.getParameterMap());
            redirectAttributes.addFlashAttribute("message", "OK");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", "Bad Request, Please Try Again");
        } catch (Exception exception) {
            exception.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Something went wrong, please try again later");
        }
        return "redirect:/settings";
    }
}
