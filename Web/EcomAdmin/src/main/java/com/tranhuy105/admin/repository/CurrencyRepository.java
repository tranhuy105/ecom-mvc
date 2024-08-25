package com.tranhuy105.admin.repository;

import com.tranhuy105.common.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
}
