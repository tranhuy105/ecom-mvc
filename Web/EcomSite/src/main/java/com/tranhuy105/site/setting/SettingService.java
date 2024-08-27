package com.tranhuy105.site.setting;

import com.tranhuy105.common.entity.Setting;
import com.tranhuy105.common.entity.SettingCategory;

import java.util.List;

public interface SettingService {
    String getSettingByKey(String key);

    List<Setting> findAll();
}
