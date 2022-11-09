package cn.chien.security.config;

import cn.chien.properties.SecurityProperties;
import cn.chien.security.CsrfSecurityRequestMatcher;
import cn.chien.security.access.FormLoginAuthenticationSuccessHandler;
import cn.chien.security.error.AccessEntryPoint;
import cn.chien.security.error.CustomAccessDeniedHandler;
import cn.chien.session.config.CustomSessionInformationExpiredStrategy;
import cn.chien.web.handler.RequestMappingHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import javax.sql.DataSource;

/**
 * @author qian.diqi
 * @date 2022/7/5
 */
@Configuration
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter implements Ordered {

    private final AccessEntryPoint accessEntryPoint;

    private final SecurityProperties securityProperties;

    private final CustomSessionInformationExpiredStrategy customSessionInformationExpiredStrategy;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    
    private final RequestMappingHandler requestMappingHandler;
    
    private final DataSource dataSource;
    
    private final UserDetailsService userDetailsService;
    
    private final FormLoginAuthenticationSuccessHandler authenticationSuccessHandler;

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
                .and().cors()
                .and().csrf().requireCsrfProtectionMatcher(new CsrfSecurityRequestMatcher(requestMappingHandler))
                .and().rememberMe()
                    .rememberMeParameter("rememberMe")
                    .tokenRepository(persistentTokenRepository())
                    .userDetailsService(userDetailsService)
                    .authenticationSuccessHandler(this.authenticationSuccessHandler);
        http.sessionManagement().sessionFixation().migrateSession()
                .maximumSessions(securityProperties.getSession().getMaxSession())
                .maxSessionsPreventsLogin(securityProperties.getSession().getKickOutAfter())
                .expiredSessionStrategy(customSessionInformationExpiredStrategy);
        http.headers().frameOptions().sameOrigin();
    }
    
    private PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE - 10;
    }

}
