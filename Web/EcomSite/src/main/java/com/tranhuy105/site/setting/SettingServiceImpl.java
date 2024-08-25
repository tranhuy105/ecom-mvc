package com.tranhuy105.site.setting;

import com.tranhuy105.common.entity.Setting;
import com.tranhuy105.common.entity.SettingCategory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {
    private final SettingRepository settingRepository;
    private static final long DEFAULT_REFRESH_INTERVAL = 1800000;
    private LocalDateTime lastCacheRefreshTime;
    private final List<Setting> settingsCache = new ArrayList<>();

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledFuture;
    private long currentInterval = DEFAULT_REFRESH_INTERVAL;

    @PostConstruct
    public void init() {
        loadSettingsIntoCache();
        this.currentInterval = getCacheRefreshInterval();
        startDynamicScheduling();
    }

    private void loadSettingsIntoCache() {
        List<Setting> settings = settingRepository.findAll();
        if (settings.isEmpty()) {
            log.error("Empty settings.");
            throw new RuntimeException("No settings found in the repository.");
        }
        settingsCache.clear();
        settingsCache.addAll(settings);
        lastCacheRefreshTime = getCurrentUTCDateTime();
        log.info("Settings loaded successfully");
    }

    private void startDynamicScheduling() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduleCacheRefresh();
    }

    private void scheduleCacheRefresh() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(false);
        }

        scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
            try {
                LocalDateTime lastUpdated = settingRepository.findMaxLastUpdated().atZone(ZoneId.of("UTC")).toLocalDateTime();
                if (lastUpdated.isAfter(lastCacheRefreshTime)) {
                    loadSettingsIntoCache();
                    updateIntervalIfChanged();
                    log.info("Detected new changes in setting, perform refreshing cache");
                }
            } catch (Exception e) {
                log.error("Error occurred while refreshing cache", e);
            }
        }, currentInterval, currentInterval, TimeUnit.MILLISECONDS);

        log.info("Cache refresh scheduled with interval: " + currentInterval);
    }

    @Override
    public String getSettingByKey(String key) {
        return this.settingsCache.stream()
                .filter(setting -> setting.getKey().equals(key))
                .map(Setting::getValue)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Setting> findAll() {
        return new ArrayList<>(settingsCache);
    }

    @Override
    public SettingCategory getCategoryForKey(String key) {
        return this.settingsCache.stream()
                .filter(setting -> setting.getKey().equals(key))
                .map(Setting::getCategory)
                .findFirst()
                .orElse(null);
    }

    private long getCacheRefreshInterval() {
        String interval = getSettingByKey("CACHE_REFRESH_INTERVAL");
        try {
            long parsedInterval = Long.parseLong(interval);
            if (parsedInterval < 10000) {
                log.warn("Cache refresh interval is too small. Using default value of " + DEFAULT_REFRESH_INTERVAL + " milliseconds.");
                return DEFAULT_REFRESH_INTERVAL;
            }
            return parsedInterval;
        } catch (NumberFormatException e) {
            log.warn("Invalid cache refresh interval format. Using default value of " + DEFAULT_REFRESH_INTERVAL + " milliseconds.");
            return DEFAULT_REFRESH_INTERVAL;
        }
    }

    @PreDestroy
    public void shutdownScheduler() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    public void updateIntervalIfChanged() {
        long newInterval = getCacheRefreshInterval();
        if (newInterval != currentInterval) {
            currentInterval = newInterval;
            scheduleCacheRefresh();
        }
    }

    private static LocalDateTime getCurrentUTCDateTime() {
        ZonedDateTime utcNow = ZonedDateTime.now(ZoneOffset.UTC);
        return utcNow.toLocalDateTime();
    }
}
