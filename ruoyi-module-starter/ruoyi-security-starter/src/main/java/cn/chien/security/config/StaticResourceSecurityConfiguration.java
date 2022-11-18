package cn.chien.security.config;

import cn.chien.security.filter.StaticResourceFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.web.filter.CorsFilter;

/**
 * @author qian.diqi
 * @date 2022/7/5
 */
@Configuration
public class StaticResourceSecurityConfiguration extends WebSecurityConfigurerAdapter implements Ordered {

    private static final String STATIC_RESOURCE = "/static/**|/modules/**|/frm/**|/i18n/**|/styles/**|/favicon.ico|/**/*.jpg|"
            + "/**/*.html|/**/*.js|/**/*.css|/**/*.map|/**/*.hbs|/**/*.woff|/**/*.woff2|/**/*.ttf|/**/*.eot|/**/*.svg|/**/*.png";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] arrs = STATIC_RESOURCE.split("\\|");
        http.setSharedObject(RequestCache.class, new NullRequestCache());
        http.requestMatchers().antMatchers(arrs)
                .and().cors()
                .and().addFilterAfter(new StaticResourceFilter(), CorsFilter.class);
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE - 20;
    }
}
