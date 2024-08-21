package com.tranhuy105.admin.user;

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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    public static final int PAGE_SIZE= 4;

    public List<User> findAllWithRole(List<User> users) {
        return userRepository.findUsersWithRoles(users);
    }

    public Page<User> findByPage(int page, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);
        if (keyword == null) {
            return userRepository.findAllUsers(pageable);
        }

        return userRepository.findAll(keyword, pageable);
    }

    public List<Role> findAllRole() {
        return roleRepository.findAll();
    }

    private void handleAvatarUpload(User user, MultipartFile avatar) throws IOException {
        if (avatar != null && !avatar.isEmpty()) {
            String fileName = validateAndGetFilename(avatar);
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
    public void save(User user, MultipartFile avatar) throws IOException {
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

    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    public boolean deleteById(Integer id) {
        if (!exist(id)) {
            return false;
        }

        userRepository.deleteById(id);

        return true;
    }

    public boolean exist(Integer id) {
        Long count = userRepository.countById(id);
        return count != null && count > 0;
    }


    private void handleEncodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    private String validateAndGetFilename(MultipartFile avatar) {
        String originalFilename = avatar.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }
        if (originalFilename.length() > 60) {
            throw new IllegalArgumentException("Invalid filename, accept maximum 60 characters");
        }
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == originalFilename.length() - 1) {
            throw new IllegalArgumentException("File extension cannot be determined");
        }

        String fileExtension = originalFilename.substring(lastDotIndex + 1).toLowerCase();
        if (!fileExtension.matches("jpg|jpeg|png")) {
            throw new IllegalArgumentException("Invalid file type. Only jpg, jpeg, and png are allowed.");
        }
        return UUID.randomUUID() + "." + fileExtension;
    }
}
