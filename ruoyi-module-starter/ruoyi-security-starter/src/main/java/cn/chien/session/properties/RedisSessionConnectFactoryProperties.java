package cn.chien.session.properties;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author qian.diqi
 * @date 2022/7/11
 */
@ConfigurationProperties(prefix = "spring.session.redis.connect")
public class RedisSessionConnectFactoryProperties extends RedisProperties {
    
}
