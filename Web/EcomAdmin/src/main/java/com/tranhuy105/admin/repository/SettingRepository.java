package com.tranhuy105.admin.repository;

import com.tranhuy105.common.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface SettingRepository extends JpaRepository<Setting, String> {
    List<Setting> findAllByKeyIn(Set<String> keys);
}
