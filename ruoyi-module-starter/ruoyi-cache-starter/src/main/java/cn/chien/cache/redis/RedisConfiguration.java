package cn.chien.cache.redis;

import cn.chien.cache.CacheManagerProperties;
import cn.chien.cache.CacheSpec;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.BatchStrategies;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * @author qian.diqi
 * @date 2022/7/11
 */
@ConditionalOnProperty(prefix = "spring.cache", name = "type", havingValue = "REDIS", matchIfMissing = true)
@ConditionalOnClass(RedisOperations.class)
@EnableConfigurationProperties({DefaultRedisProperties.class, CacheManagerProperties.class})
@Configuration
public class RedisConfiguration {
    
    private CacheManagerProperties cacheManagerProperties;
    
    @Autowired
    public void setCacheManagerProperties(CacheManagerProperties cacheManagerProperties) {
        this.cacheManagerProperties = cacheManagerProperties;
    }
    
    @Bean
    @LettuceConnectionFactory
    public RedisConnectionFactory lettuceConnectionFactory(ObjectProvider<DefaultRedisProperties> redisProperties) {
        RedisProperties properties = redisProperties.getIfAvailable();
        return new LettuceConnectionFactoryBuilder(properties).build();
    }
    
    @Bean
    public <K, V> RedisTemplate<K, V> redisTemplate(
            @LettuceConnectionFactory RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<K, V> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }
    
    @Bean
    public RedisCacheManager redisCacheManager(
            @LettuceConnectionFactory RedisConnectionFactory redisConnectionFactory) {
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .prefixCacheNameWith("RuoYi:").disableCachingNullValues();
        
        // 默认批量删除策略使用keys命令，这里替换为scan命令
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(
                        RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory, BatchStrategies.scan(1000)))
                .cacheDefaults(config).transactionAware();
        
        for (CacheSpec cacheSpec : cacheManagerProperties.getManager()) {
            RedisCacheConfiguration redisCache = RedisCacheConfiguration.defaultCacheConfig()
                    .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                    .serializeValuesWith(
                            RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                    .prefixCacheNameWith("RuoYi:").disableCachingNullValues();
            if (cacheSpec.getMaxInterval() != null && cacheSpec.getMaxInterval() > 0) {
                redisCache.entryTtl(Duration.ofSeconds(cacheSpec.getMaxInterval()));
            }
            builder.withCacheConfiguration(cacheSpec.getNamespace(), redisCache);
        }
        return builder.build();
    }
}
