package com.tranhuy105.admin.repository;

import com.tranhuy105.admin.dto.CustomerDTO;
import com.tranhuy105.admin.dto.CustomerDetailDTO;
import com.tranhuy105.common.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByEmail(String email);

    @Query("SELECT " +
            "c.id AS id, " +
            "c.email AS email, " +
            "c.firstName AS firstName, " +
            "c.lastName AS lastName, " +
            "c.phoneNumber AS phoneNumber, " +
            "c.enabled AS enabled, " +
            "c.lastLoginAt AS lastLoginAt, " +
            "c.profilePictureUrl AS profilePictureUrl, " +
            "c.dateOfBirth AS dateOfBirth, " +
            "c.createdAt AS createdAt, " +
            "c.updatedAt AS updatedAt, " +
            "c.authenticationType AS authenticationType " +
            "FROM Customer c " +
            "WHERE (:search IS NULL OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:enabled IS NULL OR c.enabled = :enabled)")
    Page<CustomerDTO> findAllCustomer(Pageable pageable, String search, Boolean enabled);

    @Query("SELECT " +
            "c.id AS id, " +
            "c.email AS email, " +
            "c.firstName AS firstName, " +
            "c.lastName AS lastName, " +
            "c.phoneNumber AS phoneNumber, " +
            "c.enabled AS enabled, " +
            "c.lastLoginAt AS lastLoginAt, " +
            "c.profilePictureUrl AS profilePictureUrl, " +
            "c.dateOfBirth AS dateOfBirth, " +
            "c.createdAt AS createdAt, " +
            "c.updatedAt AS updatedAt, " +
            "c.authenticationType AS authenticationType, " +
            "COUNT(a.id) AS addressCount, " +
            "(SELECT COUNT(o.id) FROM Order o WHERE o.customer.id = c.id) AS orderCount, " +
            "(SELECT COALESCE(SUM(o.finalAmount), 0) FROM Order o WHERE o.customer.id = c.id) AS totalSpent " +
            "FROM Customer c " +
            "LEFT JOIN c.addresses a " +
            "WHERE c.id = :customerId " +
            "GROUP BY c.id")
    Optional<CustomerDetailDTO> findCustomerDetailById(Integer customerId);
}
