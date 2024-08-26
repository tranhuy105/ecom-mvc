package com.tranhuy105.admin.repository;

import com.tranhuy105.common.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Integer> {
    @Query("SELECT c FROM Country c ORDER BY c.name")
    List<Country> findAllOrderByName();
}
