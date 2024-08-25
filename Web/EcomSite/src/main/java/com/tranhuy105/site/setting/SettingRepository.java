package com.tranhuy105.site.setting;

import com.tranhuy105.common.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface SettingRepository extends JpaRepository<Setting, String> {

    @Query("SELECT MAX(s.lastUpdated) FROM Setting s")
    LocalDateTime findMaxLastUpdated();
}
