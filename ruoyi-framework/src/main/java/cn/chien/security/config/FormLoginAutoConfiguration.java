package cn.chien.security.config;

import cn.chien.properties.SecurityProperties;
import cn.chien.security.access.FormLoginAuthenticationFailureHandler;
import cn.chien.security.access.FormLoginAuthenticationProvider;
import cn.chien.security.access.FormLoginAuthenticationSuccessHandler;
import cn.chien.security.filter.VerificationCodeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

/**
 * @author qian.diqi
 * @date 2022/7/4
 */
@Configuration
public class FormLoginAutoConfiguration extends WebSecurityConfigurerAdapter implements Ordered {

    @Autowired
    private FormLoginAuthenticationProvider formLoginAuthenticationProvider;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private FormLoginAuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private FormLoginAuthenticationFailureHandler authenticationFailureHandler;

    protected FormLoginAutoConfiguration() {
        super(true);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(formLoginAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.setSharedObject(RequestCache.class, new NullRequestCache());
        http.requestMatchers()
                .antMatchers(HttpMethod.POST, "/login")
                .antMatchers(HttpMethod.GET, "/logout")
            .and().securityContext()
            .and().anonymous()
            .and().formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .permitAll()
                .successHandler(this.authenticationSuccessHandler)
                .failureHandler(this.authenticationFailureHandler)
            .and().logout().invalidateHttpSession(true)
                .deleteCookies("SESSION")
//                .addLogoutHandler(portalLogoutHandler)
            .and().authorizeRequests()
                .anyRequest().authenticated()
            .and()
                .addFilterBefore(new VerificationCodeFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling().and().cors();
        http.sessionManagement().sessionFixation().migrateSession()
                .maximumSessions(securityProperties.getSession().getMaxSession())
                .maxSessionsPreventsLogin(securityProperties.getSession().getKickOutAfter());
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
