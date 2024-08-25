package com.tranhuy105.admin.service;

import com.tranhuy105.common.entity.Role;
import com.tranhuy105.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {
    int getPageSize();

    List<User> findAllWithRole(List<User> users);

    Page<User> findByPage(int page, String keyword);

    List<Role> findAllRole();

    @Transactional
    void save(User user, MultipartFile avatar) throws IOException;

    User updateAccount(Authentication authentication, User userUpdate, MultipartFile file) throws IOException;

    boolean isEmailUnique(Integer id, String email);

    Optional<User> findById(Integer id);

    boolean deleteById(Integer id);

    boolean exist(Integer id);
}
