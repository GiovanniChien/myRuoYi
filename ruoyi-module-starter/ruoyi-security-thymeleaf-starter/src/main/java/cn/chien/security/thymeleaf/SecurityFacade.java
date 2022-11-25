package cn.chien.security.thymeleaf;

import cn.chien.security.util.PrincipalUtil;

import java.util.Collection;

/**
 * @author qiandq3
 * @date 2022/11/21
 */
public final class SecurityFacade {
    
    public static boolean hasAllPermissions(Collection<String> permissions) {
        if (permissions.isEmpty()) {
            return false;
        }
        
        for (final String permission : permissions) {
            if (!PrincipalUtil.isPermitted(permission)) {
                return false;
            }
        }
        return true;
    }
    
}
