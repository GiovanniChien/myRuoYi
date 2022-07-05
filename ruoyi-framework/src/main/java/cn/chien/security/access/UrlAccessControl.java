package cn.chien.security.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public boolean check(Authentication auth, HttpServletRequest request) throws Exception {
        if (!auth.isAuthenticated() || AnonymousAuthenticationToken.class.isAssignableFrom(auth.getClass())) {
            logger.error("User not login in.");
            return false;
        }
        return true;
    }

}
