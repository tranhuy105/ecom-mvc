package com.tranhuy105.admin.user;

import com.tranhuy105.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findUserByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id")
    @NonNull
    Optional<User> findById(@NonNull Integer id);

    @Query("SELECT u FROM User u")
    Page<User> findAllUsers(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.email LIKE %:search%")
    Page<User> findAll(String search, Pageable pageable);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u IN :users")
    List<User> findUsersWithRoles(@Param("users") List<User> users);

    Long countById(Integer id);
}
