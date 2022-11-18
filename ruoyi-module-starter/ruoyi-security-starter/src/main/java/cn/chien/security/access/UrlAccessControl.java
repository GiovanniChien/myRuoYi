package cn.chien.security.access;

import cn.chien.annotation.SkipSessionCheck;
import cn.chien.security.handler.RequestMappingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qian.diqi
 * @date 2022/7/5
 */
@Component
public class UrlAccessControl {

    private static final Logger logger = LoggerFactory.getLogger(UrlAccessControl.class);

    @Autowired
    private RequestMappingHandler requestMappingHandler;

    public boolean check(Authentication auth, HttpServletRequest request) throws Exception {
        if (requestMappingHandler.hasAnnotation(request, SkipSessionCheck.class, RequestMappingHandler.AnnotationScope.ALL)) {
            return true;
        }
        if (!auth.isAuthenticated() || AnonymousAuthenticationToken.class.isAssignableFrom(auth.getClass())) {
            logger.error("User not login in.");
            return false;
        }
        return true;
    }

}
