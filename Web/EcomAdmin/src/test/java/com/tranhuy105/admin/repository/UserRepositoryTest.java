package com.tranhuy105.admin.repository;

import com.tranhuy105.common.entity.Role;
import com.tranhuy105.common.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Test
    public void testCreateUser() {
        User admin = new User();
        Role adminRole = new Role();
        adminRole.setId(1);

        admin.setEmail("tranhuy105@admin.com");
        admin.setPassword(new BCryptPasswordEncoder().encode("123456"));
        admin.setFirstName("Huy");
        admin.setLastName("Trần");
        admin.addRole(adminRole);

        User saved = repository.save(admin);
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("tranhuy105@admin.com");
        assertThat(saved.getRoles()).contains(adminRole);
    }

    @Test
    public void testFindUserByEmail() {
        User admin = new User();
        admin.setEmail("tranhuy105@admin.com");
        admin.setPassword(new BCryptPasswordEncoder().encode("123456"));
        admin.setFirstName("Huy");
        admin.setLastName("Trần");
        repository.save(admin);

        Optional<User> foundUser = repository.findUserByEmail("tranhuy105@admin.com");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("tranhuy105@admin.com");
    }

    @Test
    public void testFindUserByEmailNotFound() {
        Optional<User> foundUser = repository.findUserByEmail("nonexistent@admin.com");
        assertThat(foundUser).isNotPresent();
    }

    @Test
    public void testFindAllUsers() {
        User user1 = new User();
        user1.setEmail("user1@admin.com");
        user1.setPassword(new BCryptPasswordEncoder().encode("123456"));
        user1.setFirstName("User1");
        user1.setLastName("One");
        repository.save(user1);

        User user2 = new User();
        user2.setEmail("user2@admin.com");
        user2.setPassword(new BCryptPasswordEncoder().encode("123456"));
        user2.setFirstName("User2");
        user2.setLastName("Two");
        repository.save(user2);

        Page<User> users = repository.findAllUsers(PageRequest.of(0, 10));
        assertThat(users.getContent()).hasSize(2);
        assertThat(users.getContent()).extracting(User::getEmail)
                .contains("user1@admin.com", "user2@admin.com");
    }

    @Test
    public void testFindAllUsersWithSearch() {
        User user1 = new User();
        user1.setEmail("searchuser1@admin.com");
        user1.setPassword(new BCryptPasswordEncoder().encode("123456"));
        user1.setFirstName("SearchUser1");
        user1.setLastName("One");
        repository.save(user1);

        User user2 = new User();
        user2.setEmail("searchuser2@admin.com");
        user2.setPassword(new BCryptPasswordEncoder().encode("123456"));
        user2.setFirstName("SearchUser2");
        user2.setLastName("Two");
        repository.save(user2);

        Page<User> users = repository.findAll("searchuser", PageRequest.of(0, 10));
        assertThat(users.getContent()).hasSize(2);
        assertThat(users.getContent()).extracting(User::getEmail)
                .contains("searchuser1@admin.com", "searchuser2@admin.com");
    }

    @Test
    public void testFindUsersWithRoles() {
        User user1 = new User();
        Role role1 = new Role();
        role1.setId(1);
        user1.setEmail("user1@admin.com");
        user1.setPassword(new BCryptPasswordEncoder().encode("123456"));
        user1.setFirstName("User1");
        user1.setLastName("One");
        user1.addRole(role1);
        User savedUser1 = repository.save(user1);

        User user2 = new User();
        Role role2 = new Role();
        role2.setId(2);
        user2.setEmail("user2@admin.com");
        user2.setPassword(new BCryptPasswordEncoder().encode("123456"));
        user2.setFirstName("User2");
        user2.setLastName("Two");
        user2.addRole(role2);
        User savedUser2 = repository.save(user2);

        List<User> usersWithRoles = repository.findUsersWithRoles(List.of(savedUser1, savedUser2));
        assertThat(usersWithRoles).hasSize(2);
        assertThat(usersWithRoles).extracting(User::getEmail)
                .contains("user1@admin.com", "user2@admin.com");
    }

    @Test
    public void testCountById() {
        User user = new User();
        user.setEmail("countuser@admin.com");
        user.setPassword(new BCryptPasswordEncoder().encode("123456"));
        user.setFirstName("CountUser");
        user.setLastName("One");
        User savedUser = repository.save(user);

        Long count = repository.countById(savedUser.getId());
        assertThat(count).isEqualTo(1L);
    }
}
