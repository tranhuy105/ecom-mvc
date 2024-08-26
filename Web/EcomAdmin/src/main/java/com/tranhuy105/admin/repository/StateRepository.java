package com.tranhuy105.admin.repository;

import com.tranhuy105.common.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface StateRepository extends JpaRepository<State, Integer> {
    List<State> findAllByCountryId(@NonNull Integer countryId);
}
