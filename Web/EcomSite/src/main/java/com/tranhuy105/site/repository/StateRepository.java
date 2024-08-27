package com.tranhuy105.site.repository;

import com.tranhuy105.common.entity.State;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StateRepository extends CrudRepository<State, Integer> {
    @Query("SELECT s.name FROM State s WHERE s.country.id = :countryId")
    List<String> findAllByCountry(Integer countryId);
}
