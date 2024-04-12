package cn.chien.security.config;

import cn.chien.security.filter.StaticResourceFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * @author qian.diqi
 * @date 2022/7/5
 */
@Configuration
public class StaticResourceSecurityConfiguration {

    private static final String STATIC_RESOURCE = "/static/**|/modules/**|/frm/**|/i18n/**|/styles/**|/favicon.ico|/**/*.jpg|"
            + "/**/*.html|/**/*.js|/**/*.css|/**/*.map|/**/*.hbs|/**/*.woff|/**/*.woff2|/**/*.ttf|/**/*.eot|/**/*.svg|/**/*.png";
    
    @Bean(name = "staticResourceSecurityFilterChain")
    @Order(Integer.MAX_VALUE - 20)
    public SecurityFilterChain staticResourceSecurityFilterChain(HttpSecurity http) throws Exception {
        String[] arrs = STATIC_RESOURCE.split("\\|");
        AntPathRequestMatcher[] antPathRequestMatchers = Arrays.stream(arrs).map(AntPathRequestMatcher::new)
                .toArray(AntPathRequestMatcher[]::new);
        http.setSharedObject(RequestCache.class, new NullRequestCache());
        http
                .securityMatchers(requestMatcherConfigurer -> requestMatcherConfigurer.requestMatchers(antPathRequestMatchers))
                .cors(Customizer.withDefaults())
                .addFilterAfter(new StaticResourceFilter(), CorsFilter.class)
                .headers(headersConfigurer -> headersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        return http.build();
    }
}
