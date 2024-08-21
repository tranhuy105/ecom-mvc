package com.tranhuy105.admin.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.tranhuy105.common.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
}
