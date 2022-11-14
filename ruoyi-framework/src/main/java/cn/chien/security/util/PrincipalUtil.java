package cn.chien.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author qiandq3
 * @date 2022/11/11
 */
public final class PrincipalUtil {
    
    public static boolean hasLogin() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.isAuthenticated();
    }

    public static Authentication getAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return null;
        }
        return context.getAuthentication();
    }


}
