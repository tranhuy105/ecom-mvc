package com.tranhuy105.admin.service;

import com.tranhuy105.common.entity.Currency;
import com.tranhuy105.common.entity.Setting;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface SettingService {
    List<Setting> findAll();

    List<Currency> findAllCurrency();

    @Transactional
    void saveSetting(Map<String, String[]> parameterMap);
}
