package cn.chien.session.config;

import cn.chien.session.annotation.EnableRedisHttpSessionWithoutListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @author qian.diqi
 * @date 2022/7/11
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "spring.session", name = "store-type", havingValue = "redis")
@EnableRedisHttpSessionWithoutListener
public class RedisSessionConfiguration {



}
