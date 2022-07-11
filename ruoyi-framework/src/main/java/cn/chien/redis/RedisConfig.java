package cn.chien.redis;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;

/**
 * @author qian.diqi
 * @date 2022/7/11
 */
@ConditionalOnProperty(prefix = "spring.redis", name = "enable", havingValue = "true")
@ConditionalOnClass(RedisOperations.class)
@EnableConfigurationProperties(DefaultRedisProperties.class)
@Configuration
public class RedisConfig {

    @Bean
    @cn.chien.redis.LettuceConnectionFactory
    public RedisConnectionFactory lettuceConnectionFactory(ObjectProvider<DefaultRedisProperties> redisProperties) {
        RedisProperties properties = redisProperties.getIfAvailable();
        return new LettuceConnectionFactoryBuilder(properties).build();
    }

}
