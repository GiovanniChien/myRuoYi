package cn.chien.session.config;

import cn.chien.redis.LettuceConnectionFactoryBuilder;
import cn.chien.session.properties.RedisSessionConnectFactoryProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.SpringSessionRedisConnectionFactory;

/**
 * @author qian.diqi
 * @date 2022/7/11
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "spring.session", name = "store-type", havingValue = "redis")
@EnableConfigurationProperties(RedisSessionConnectFactoryProperties.class)
public class RedisSessionConnectionFactoryConfiguration {

    private RedisSessionConnectFactoryProperties properties;

    @Autowired
    public void setProperties(RedisSessionConnectFactoryProperties properties) {
        this.properties = properties;
    }

    @Bean(value = "springSessionRedisConnectionFactory")
    @SpringSessionRedisConnectionFactory
    public RedisConnectionFactory springSessionRedisConnectionFactory() {
        LettuceConnectionFactoryBuilder lettuceConnectionFactoryBuilder = new LettuceConnectionFactoryBuilder(properties);
        return lettuceConnectionFactoryBuilder.build();
    }

}
