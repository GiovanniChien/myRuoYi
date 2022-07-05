package cn.chien.session.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.SessionRepository;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qian.diqi
 * @date 2022/7/4
 */
@Configuration
public class SessionConfiguration {

    @Bean
    @ConditionalOnMissingBean({SessionRegistry.class})
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    @ConditionalOnMissingBean({SessionRepository.class})
    public MapSessionRepository sessionRepository() {
        MapSessionRepository sessionRepository = new MapSessionRepository(new ConcurrentHashMap());
        sessionRepository.setDefaultMaxInactiveInterval(3600);
        return sessionRepository;
    }

//    @Bean
//    @ConditionalOnMissingBean({CookieSerializer.class})
//    public CookieSerializer cookieSerializer(SecurityProperties securityProperties) {
//        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
//        CookiesProperties cookiesProperties = this.cookiesProperties();
//        cookieSerializer.setCookieName(cookiesProperties.getCookie().getName());
//        if (null == cookiesProperties.getCookie().getPath()) {
//            cookieSerializer.setCookiePath("/");
//        } else {
//            cookieSerializer.setCookiePath(cookiesProperties.getCookie().getPath());
//        }
//
//        cookieSerializer.setCookieMaxAge(cookiesProperties.getCookie().getMaxAge());
//        cookieSerializer.setDomainName(cookiesProperties.getCookie().getDomain());
//        cookieSerializer.setUseBase64Encoding(false);
//        boolean isCookieSecure = securityProperties.isCookieSecure();
//        if (isCookieSecure) {
//            cookieSerializer.setUseSecureCookie(isCookieSecure);
//        }
//
//        cookieSerializer.setSameSite((String)null);
//        return cookieSerializer;
//    }

}
