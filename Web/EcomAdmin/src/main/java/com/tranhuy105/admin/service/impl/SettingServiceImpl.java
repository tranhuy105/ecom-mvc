package com.tranhuy105.admin.service.impl;

import com.tranhuy105.admin.repository.CurrencyRepository;
import com.tranhuy105.admin.repository.SettingRepository;
import com.tranhuy105.admin.service.SettingService;
import com.tranhuy105.common.entity.Currency;
import com.tranhuy105.common.entity.Setting;
import com.tranhuy105.common.entity.SettingCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {
    private final SettingRepository settingRepository;
    private final CurrencyRepository currencyRepository;

    @Override
    public List<Setting> findAll() {
        return settingRepository.findAll();
    }

    @Override
    public List<Currency> findAllCurrency() {
        return currencyRepository.findAll().stream().sorted(Comparator.comparing(Currency::getName)).toList();
    }

    @Override
    @Transactional
    public void saveSetting(Map<String, String[]> parameterMap) {
        List<Setting> settings = new ArrayList<>();
        Set<String> keys = parameterMap.keySet();

        List<Setting> existingSettings = settingRepository.findAllByKeyIn(keys);
        Map<String, Setting> existingSettingsMap = existingSettings.stream()
                .collect(Collectors.toMap(Setting::getKey, Function.identity()));

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] paramValues = entry.getValue();
            StringBuilder value = new StringBuilder();
            for (String paramValue : paramValues) {
                value.append(paramValue);
            }

            if (value.toString().isBlank()) {
                continue;
            }

            Setting setting = existingSettingsMap.get(key);
            if (setting != null)  {
                setting.setValue(value.toString());
                settings.add(setting);
            }
        }

        settingRepository.saveAll(settings);
    }

}
