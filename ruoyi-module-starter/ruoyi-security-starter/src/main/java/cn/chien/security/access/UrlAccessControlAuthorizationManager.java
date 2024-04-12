package cn.chien.security.access;

import cn.chien.annotation.SkipSessionCheck;
import cn.chien.security.handler.RequestMappingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * @author qian.diqi
 * @date 2022/7/5
 */
@Component
public class UrlAccessControlAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final Logger logger = LoggerFactory.getLogger(UrlAccessControlAuthorizationManager.class);

    @Autowired
    private RequestMappingHandler requestMappingHandler;
    
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        if (requestMappingHandler.hasAnnotation(context.getRequest(), SkipSessionCheck.class, RequestMappingHandler.AnnotationScope.ALL)) {
            return new AuthorizationDecision(true);
        }
        Authentication auth = authentication.get();
        if (!auth.isAuthenticated() || AnonymousAuthenticationToken.class.isAssignableFrom(auth.getClass())) {
            logger.error("User not login in.");
            return new AuthorizationDecision(false);
        }
        return new AuthorizationDecision(true);
    }
}
