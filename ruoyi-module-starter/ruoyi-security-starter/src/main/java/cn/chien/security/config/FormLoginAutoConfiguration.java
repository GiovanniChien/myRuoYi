package cn.chien.security.config;

import cn.chien.security.CsrfSecurityRequestMatcher;
import cn.chien.security.SecurityProperties;
import cn.chien.security.access.FormLoginAuthenticationFailureHandler;
import cn.chien.security.access.FormLoginAuthenticationProvider;
import cn.chien.security.access.FormLoginAuthenticationSuccessHandler;
import cn.chien.security.filter.VerificationCodeFilter;
import cn.chien.security.handler.RequestMappingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import javax.sql.DataSource;

/**
 * @author qian.diqi
 * @date 2022/7/4
 */
@Configuration(proxyBeanMethods = false)
public class FormLoginAutoConfiguration {

    @Autowired
    private FormLoginAuthenticationProvider formLoginAuthenticationProvider;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private FormLoginAuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private FormLoginAuthenticationFailureHandler authenticationFailureHandler;
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private RequestMappingHandler requestMappingHandler;
    
    @Bean(name = "formLoginSecurityFilterChain")
    @Order(10)
    public SecurityFilterChain formLoginSecurityFilterChain(HttpSecurity http) throws Exception {
        http.setSharedObject(RequestCache.class, new NullRequestCache());
        http
                .securityMatchers(requestMatcherConfigurer -> requestMatcherConfigurer.requestMatchers("/login", "/logout"))
                .authorizeHttpRequests((authorizeRequests) -> {
                    authorizeRequests.anyRequest().permitAll();
                })
                .securityContext(Customizer.withDefaults())
                .anonymous(Customizer.withDefaults())
                .formLogin((formLogin) -> {
                    formLogin.loginPage("/login")
                            .loginProcessingUrl("/login")
                            .permitAll()
                            .successHandler(this.authenticationSuccessHandler)
                            .failureHandler(this.authenticationFailureHandler);
                })
                .logout((logout) -> {
                    logout.invalidateHttpSession(true).deleteCookies("SESSION");
                })
                .addFilterBefore(new VerificationCodeFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(Customizer.withDefaults())
                .cors(Customizer.withDefaults())
                .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.requireCsrfProtectionMatcher(new CsrfSecurityRequestMatcher(requestMappingHandler)))
                .rememberMe((rememberMe) -> {
                    rememberMe.rememberMeParameter("rememberMe")
                            .tokenRepository(persistentTokenRepository())
                            .userDetailsService(userDetailsService)
                            .authenticationSuccessHandler(this.authenticationSuccessHandler);
                })
                .sessionManagement((sessionManagementCustomizer) -> {
                    sessionManagementCustomizer.sessionFixation().migrateSession()
                            .maximumSessions(securityProperties.getSession().getMaxSession())
                            .maxSessionsPreventsLogin(securityProperties.getSession().getKickOutAfter());
                })
                .authenticationProvider(formLoginAuthenticationProvider);
        return http.build();
    }
    
    private PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }
    
}
