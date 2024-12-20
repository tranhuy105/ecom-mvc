package com.tranhuy105.admin.service.impl;

import com.tranhuy105.admin.exception.DuplicateEmailException;
import com.tranhuy105.admin.repository.RoleRepository;
import com.tranhuy105.admin.repository.UserRepository;
import com.tranhuy105.admin.service.UserService;
import com.tranhuy105.admin.utils.AuthUtil;
import com.tranhuy105.admin.utils.FileUploadUtil;
import com.tranhuy105.common.entity.Role;
import com.tranhuy105.common.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    public static final int PAGE_SIZE= 4;

    @Override
    public int getPageSize() {
        return PAGE_SIZE;
    }

    @Override
    public List<User> findAllWithRole(List<User> users) {
        return userRepository.findUsersWithRoles(users);
    }

    @Override
    public Page<User> findByPage(int page, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);
        if (keyword == null) {
            return userRepository.findAllUsers(pageable);
        }

        return userRepository.findAll(keyword, pageable);
    }

    @Override
    public List<Role> findAllRole() {
        return roleRepository.findAll();
    }

    private void handleAvatarUpload(User user, MultipartFile avatar) throws IOException {
        if (avatar != null && !avatar.isEmpty()) {
            String fileName = FileUploadUtil.validateAndGetImageFilename(avatar);
            user.setAvatar(fileName);
            // if is creating new user
            if (user.getId() == null) {
                user = userRepository.save(user);
            }
            String uploadDir = "user-avatars/" + user.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, avatar);
        } else {
            if (user.getAvatar() != null && user.getAvatar().isEmpty()) {
                user.setAvatar(null);
            }
        }
    }

    @Transactional
    @Override
    public void save(User user, MultipartFile avatar) throws IOException {
        if (!isEmailUnique(user.getId(), user.getEmail())) {
            throw new DuplicateEmailException();
        }
        boolean isUpdatingUser = (user.getId() != null);

        if (isUpdatingUser) {
            User existingUser = userRepository.findById(user.getId()).orElse(null);

            if (existingUser == null) {
                // should never happen
                return;
            }

            if (user.getPassword().trim().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                handleEncodePassword(user);
            }

        } else {
            handleEncodePassword(user);
        }

        handleAvatarUpload(user, avatar);
        userRepository.save(user);
    }

    @Override
    public User updateAccount(Authentication authentication, User userUpdate, MultipartFile file) throws IOException {
        User userDB = AuthUtil.extractUserFromAuthentication(authentication);
        if (!userDB.getId().equals(userUpdate.getId())) {
            throw new IllegalArgumentException("Access Denied");
        }

        // prevent from update these field
        userUpdate.setId(userDB.getId());
        userUpdate.setEmail(userDB.getEmail());
        userUpdate.setRoles(userDB.getRoles());
        userUpdate.setEnabled(userDB.isEnabled());
        userUpdate.setPassword(userUpdate.getPassword().trim());

        if (!userUpdate.getPassword().isEmpty()) {
            if (userUpdate.getPassword().length() < 6) {
                throw new IllegalArgumentException("Invalid Password Length");
            }
            handleEncodePassword(userUpdate);
        } else {
            userUpdate.setPassword(userDB.getPassword());
        }

        handleAvatarUpload(userUpdate, file);
        return userRepository.save(userUpdate);
    }

    @Override
    public boolean isEmailUnique(Integer id, String email) {
        Optional<User> user = userRepository.findUserByEmail(email.trim());

        if (user.isEmpty()) return true;
        boolean isCreatingNew = (id == null);
        if (isCreatingNew) {
            return false;
        } else {
            return Objects.equals(user.get().getId(), id);
        }
    }

    @Override
    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public boolean deleteById(Integer id) {
        if (!exist(id)) {
            return false;
        }

        userRepository.deleteById(id);

        return true;
    }

    @Override
    public boolean exist(Integer id) {
        Long count = userRepository.countById(id);
        return count != null && count > 0;
    }


    private void handleEncodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }
}
