package com.tranhuy105.admin.user;

import com.tranhuy105.common.entity.Role;
import com.tranhuy105.common.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
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
        assertThat(saved.getId()).isEqualTo(1);
    }

    @Test
    public void testGetUserById() {
        User admin = repository.findById(1).orElse(null);
        assertThat(admin).isNotNull();
    }

}
