package cn.chien.security.config;

import cn.chien.properties.SecurityProperties;
import cn.chien.security.error.AccessEntryPoint;
import cn.chien.security.error.CustomAccessDeniedHandler;
import cn.chien.session.config.CustomSessionInformationExpiredStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

/**
 * @author qian.diqi
 * @date 2022/7/5
 */
@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter implements Ordered {

    @Autowired
    private AccessEntryPoint accessEntryPoint;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private CustomSessionInformationExpiredStrategy customSessionInformationExpiredStrategy;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.setSharedObject(RequestCache.class, new NullRequestCache());
        http.requestMatchers().anyRequest()
                .and().securityContext()
                .and().anonymous()
                .and().exceptionHandling()
                .authenticationEntryPoint(this.accessEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
                .and().authorizeRequests((requests) -> requests.filterSecurityInterceptorOncePerRequest(false))
                .authorizeRequests().anyRequest().access("@urlAccessControl.check(authentication, request)")
                .and().servletApi()
                .and().cors();
        http.sessionManagement().sessionFixation().migrateSession()
                .maximumSessions(securityProperties.getSession().getMaxSession())
                .maxSessionsPreventsLogin(securityProperties.getSession().getKickOutAfter())
                .expiredSessionStrategy(customSessionInformationExpiredStrategy);
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE - 10;
    }

}
