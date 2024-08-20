package com.tranhuy105.admin.user;


import com.tranhuy105.common.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleRepositoryTest {
    @Autowired
    private RoleRepository repository;

    @Test
    public void testGetAllRole() {
        List<Role> roles = (List<Role>) repository.findAll();

        assertThat(roles).isNotNull();
        assertThat(roles.size()).isGreaterThan(0);
    }
}
