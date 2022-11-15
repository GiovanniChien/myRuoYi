package cn.chien.session.config;

import cn.chien.session.annotation.EnableRedisHttpSessionWithoutListener;
import cn.chien.session.jackson.SecurityJackson2JsonRedisSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author qian.diqi
 * @date 2022/7/11
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "security.session", name = "store-type", havingValue = "redis")
@EnableRedisHttpSessionWithoutListener
public class RedisSessionConfiguration {

    @Bean("springSessionDefaultRedisSerializer")
    @ConditionalOnMissingBean(name = "springSessionDefaultRedisSerializer")
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new SecurityJackson2JsonRedisSerializer();
    }

}
