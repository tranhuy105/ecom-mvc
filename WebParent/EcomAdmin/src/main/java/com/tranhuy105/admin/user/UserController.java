package com.tranhuy105.admin.user;

import com.tranhuy105.common.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public String listAll(Model model,
                          @RequestParam(value = "page", required = false) Integer page,
                          @RequestParam(value = "q", required = false) String search) {
        page = (page == null || page < 1) ? 1 : page;
        Page<User> user = userService.findByPage(page, search);

        long startCount = (long) (page - 1) * UserService.PAGE_SIZE + 1;
        long endCount = startCount + UserService.PAGE_SIZE - 1;
        if (endCount > user.getTotalElements()){
            endCount = user.getTotalElements();
        }

        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", user.getTotalElements());
        model.addAttribute("totalPages", user.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("listUsers", userService.findAllWithRole(user.getContent()));
        model.addAttribute("q", search);
        return "users";
    }

    @GetMapping("/users/new")
    public String createNewUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("listRoles", userService.findAllRole());
        model.addAttribute("pageTitle", "Create New User");
        return "user_form";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable Integer id,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            model.addAttribute("listRoles", userService.findAllRole());
            model.addAttribute("pageTitle", "Edit User "+id);
            return "user_form";
        } else {
            redirectAttributes.addFlashAttribute("message", "Couldn't found user with id " +id);
            return "redirect:/users";
        }

    }

    @PostMapping("/users")
    public String saveUser(User user,
                           @RequestParam("image") MultipartFile avatar,
                           RedirectAttributes redirectAttributes) {
        try {
            userService.save(user, avatar);
            redirectAttributes.addFlashAttribute("message", "User has been saved successfully.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "IOException when saving user: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/users";
    }

    @PostMapping("/users/{id}")
    public String deleteUser(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        if (userService.deleteById(id)) {
            redirectAttributes.addFlashAttribute("message", "User deleted");
        } else {
            redirectAttributes.addFlashAttribute("message", "Fail to delete user with id "+id);
        }
        return "redirect:/users";
    }
}
