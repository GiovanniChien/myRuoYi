package cn.chien.cache.caffeine;

import cn.chien.cache.CacheManagerProperties;
import cn.chien.cache.CacheSpec;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author qian.diqi
 * @date 2022/7/12
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.cache", value = "type", havingValue = "CAFFEINE", matchIfMissing = true)
@EnableConfigurationProperties(CacheManagerProperties.class)
public class CaffeineConfiguration {

    private CacheManagerProperties cacheManagerProperties;

    @Autowired
    public void setCacheManagerProperties(CacheManagerProperties cacheManagerProperties) {
        this.cacheManagerProperties = cacheManagerProperties;
    }

    @Bean
    public CaffeineCacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        for (CacheSpec cacheSpec : cacheManagerProperties.getManager()) {
            Caffeine<Object, Object> caffeine = Caffeine.newBuilder();
            if (cacheSpec.getMaxInterval() != null && cacheSpec.getMaxInterval() > 0) {
                if (cacheSpec.isRefreshTtlAfterAccess()) {
                    caffeine.expireAfterAccess(cacheSpec.getMaxInterval(), TimeUnit.SECONDS);
                }
                else {
                    caffeine.expireAfterWrite(cacheSpec.getMaxInterval(), TimeUnit.SECONDS);
                }
            }
            caffeine.initialCapacity(50).maximumSize(1000);
            cacheManager.registerCustomCache(cacheSpec.getNamespace(), caffeine.build());
        }
        return cacheManager;
    }
}
