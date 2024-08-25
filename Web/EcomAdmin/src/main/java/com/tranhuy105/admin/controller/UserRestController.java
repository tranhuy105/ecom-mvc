package com.tranhuy105.admin.controller;

import com.tranhuy105.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

    @PostMapping("/users/check_email")
    public ResponseEntity<String> checkDuplicateEmail(@Param("email") String email, @Param("id") Integer id) {
        return ResponseEntity.ok(userService.isEmailUnique(id, email) ? "OK" : "Duplicated");
    }
}
