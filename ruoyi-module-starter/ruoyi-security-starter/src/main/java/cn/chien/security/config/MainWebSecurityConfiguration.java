package cn.chien.security.config;

import cn.chien.security.CsrfSecurityRequestMatcher;
import cn.chien.security.SecurityProperties;
import cn.chien.security.access.FormLoginAuthenticationSuccessHandler;
import cn.chien.security.access.UrlAccessControlAuthorizationManager;
import cn.chien.security.error.AccessEntryPoint;
import cn.chien.security.error.CustomAccessDeniedHandler;
import cn.chien.security.handler.RequestMappingHandler;
import cn.chien.security.session.CustomSessionInformationExpiredStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.AbstractRequestMatcherRegistry;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
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
public class MainWebSecurityConfiguration {

    private final AccessEntryPoint accessEntryPoint;

    private final SecurityProperties securityProperties;

    private final CustomSessionInformationExpiredStrategy customSessionInformationExpiredStrategy;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    
    private final RequestMappingHandler requestMappingHandler;
    
    private final DataSource dataSource;
    
    private final UserDetailsService userDetailsService;
    
    private final FormLoginAuthenticationSuccessHandler authenticationSuccessHandler;
    
    private final UrlAccessControlAuthorizationManager urlAccessControlAuthorizationManager;
    
    @Bean(name = "mainSecurityFilterChain")
    @Order(Integer.MAX_VALUE - 10)
    public SecurityFilterChain mainSecurityFilterChain(HttpSecurity http) throws Exception {
        http.setSharedObject(RequestCache.class, new NullRequestCache());
        http
                .securityMatchers(AbstractRequestMatcherRegistry::anyRequest)
                .authorizeHttpRequests((authorizeRequests) -> {
                    authorizeRequests
                            .anyRequest().access(urlAccessControlAuthorizationManager);
                })
                .securityContext(Customizer.withDefaults())
                .anonymous(Customizer.withDefaults())
                .exceptionHandling(exceptionHandlingCustomizer -> {
                    exceptionHandlingCustomizer.authenticationEntryPoint(accessEntryPoint).accessDeniedHandler(customAccessDeniedHandler);
                })
                .servletApi(Customizer.withDefaults())
                .cors(Customizer.withDefaults())
                .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.requireCsrfProtectionMatcher(new CsrfSecurityRequestMatcher(requestMappingHandler)))
                .rememberMe(rememberMeCustomizer -> {
                    rememberMeCustomizer.rememberMeParameter("rememberMe")
                            .tokenRepository(persistentTokenRepository())
                            .userDetailsService(userDetailsService)
                            .authenticationSuccessHandler(authenticationSuccessHandler);
                })
                .sessionManagement(sessionManagementCustomizer -> {
                    sessionManagementCustomizer
                            .sessionFixation().migrateSession()
                            .maximumSessions(securityProperties.getSession().getMaxSession())
                            .maxSessionsPreventsLogin(securityProperties.getSession().getKickOutAfter())
                            .expiredSessionStrategy(customSessionInformationExpiredStrategy);
                })
                .headers(headersCustomizer -> headersCustomizer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        return http.build();
    }
    
    private PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

}
