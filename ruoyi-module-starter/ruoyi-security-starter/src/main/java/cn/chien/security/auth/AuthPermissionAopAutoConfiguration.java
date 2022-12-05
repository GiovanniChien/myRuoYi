package cn.chien.security.auth;

import cn.chien.security.auth.aspect.AuthMethodMatcherPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiandq3
 * @date 2022/11/28
 */
@Configuration(proxyBeanMethods = false)
public class AuthPermissionAopAutoConfiguration {
    
    @Bean
    public AuthMethodMatcherPointcutAdvisor authMethodMatcherPointcutAdvisor() {
        return new AuthMethodMatcherPointcutAdvisor();
    }
    
}
