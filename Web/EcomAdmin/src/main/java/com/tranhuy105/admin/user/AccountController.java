package com.tranhuy105.admin.user;

import com.tranhuy105.admin.security.MyUserDetail;
import com.tranhuy105.admin.utils.AuthUtil;
import com.tranhuy105.common.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class AccountController {
    private final UserService userService;

    @GetMapping("/account")
    public String viewDetails(Authentication authentication,
                              Model model) {
        User loggedUser = AuthUtil.extractUserFromAuthentication(authentication);
        model.addAttribute("user", loggedUser);

        return "account_form";
    }

    @PostMapping("/account")
    public String updateAccountDetail(Authentication authentication,
                                      User updatedUser,
                                      @RequestParam("image") MultipartFile avatar,
                                      RedirectAttributes redirectAttributes) {
        try {
            User user = userService.updateAccount(authentication, updatedUser, avatar);

            MyUserDetail currentUserDetail = (MyUserDetail) authentication.getPrincipal();
            // update user object in session
            currentUserDetail.setUser(user);
            redirectAttributes.addFlashAttribute("message", "Update Successfully");
        } catch (IOException | IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", "Something went wrong");
        }
        return "redirect:/account";
    }
}
