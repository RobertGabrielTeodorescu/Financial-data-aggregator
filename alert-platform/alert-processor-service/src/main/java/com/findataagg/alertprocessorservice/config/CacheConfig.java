package com.findataagg.alertprocessorservice.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration for caching alert rules.
 * Uses Caffeine cache for high-performance in-memory caching with TTL-based expiration.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String ALERT_RULES_CACHE = "alertRules";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(ALERT_RULES_CACHE);
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .maximumSize(1000)  // Maximum 1000 cache entries
                .expireAfterWrite(5, TimeUnit.MINUTES)  // TTL: 5 minutes as safety net
                .recordStats();  // Enable cache statistics for monitoring
    }
}
