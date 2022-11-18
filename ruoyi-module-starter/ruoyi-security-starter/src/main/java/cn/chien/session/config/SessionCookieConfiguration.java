package cn.chien.session.config;

import cn.chien.security.SecurityProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.session.DefaultCookieSerializerCustomizer;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.server.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @author qian.diqi
 * @date 2022/7/12
 */
@Configuration
public class SessionCookieConfiguration {

    @Bean
    DefaultCookieSerializer cookieSerializer(SecurityProperties securityProperties,
                                             ObjectProvider<DefaultCookieSerializerCustomizer> cookieSerializerCustomizers) {
        SecurityProperties.Cookie cookie = securityProperties.getCookie();
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from(cookie::getName).to(cookieSerializer::setCookieName);
        map.from(cookie::getDomain).to(cookieSerializer::setDomainName);
        map.from(cookie::getPath).to(cookieSerializer::setCookiePath);
        map.from(cookie::getHttpOnly).to(cookieSerializer::setUseHttpOnlyCookie);
        map.from(cookie::getSecure).to(cookieSerializer::setUseSecureCookie);
        map.from(() -> cookie.getMaxAge() == -1 ? -1 : cookie.getMaxAge() * 24 * 3600).to(cookieSerializer::setCookieMaxAge);
        map.from(cookie::getSameSite).as(Cookie.SameSite::attributeValue).to(cookieSerializer::setSameSite);
        map.from(cookie::getUseBase64).to(cookieSerializer::setUseBase64Encoding);
        cookieSerializerCustomizers.orderedStream().forEach((customizer) -> customizer.customize(cookieSerializer));
        return cookieSerializer;
    }

}
