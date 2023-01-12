package cn.chien.security.auth.handler;

import cn.chien.security.auth.PermissionContextHolder;
import cn.chien.security.auth.annotation.RequiresRoles;
import cn.chien.security.auth.enums.Logical;
import cn.chien.security.exception.UnAuthorizedException;
import cn.chien.security.util.PrincipalUtil;
import cn.chien.utils.StringUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.core.AuthenticationException;

import java.lang.annotation.Annotation;

public class RequiresRolesHandler implements AuthMethodHandler {
    
    @Override
    public Class<? extends Annotation> supportAnnotation() {
        return RequiresRoles.class;
    }
    
    @Override
    public void assertAuthorized(MethodInvocation methodInvocation, Annotation annotation)
            throws AuthenticationException {
        if (!(annotation instanceof RequiresRoles rpAnnotation)) {
            return;
        }
        String[] perms = rpAnnotation.value();
        Logical logical = rpAnnotation.logical();
        PermissionContextHolder.setContext(StringUtils.join(perms, ','));
        for (String perm : perms) {
            if (!PrincipalUtil.hasRole(perm)) {
                if (Logical.AND.equals(logical)) {
                    throw new UnAuthorizedException("User does not have role [" + perm + "]");
                }
            }
            else {
                if (Logical.OR.equals(logical)) {
                    return;
                }
            }
        }
        if (Logical.OR.equals(logical)) {
            throw new UnAuthorizedException("User does not have role [" + perms[0] + "]");
        }
    }
}
