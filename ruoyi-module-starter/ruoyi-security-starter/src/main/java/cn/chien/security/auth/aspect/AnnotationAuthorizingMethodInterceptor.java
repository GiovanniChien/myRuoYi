package cn.chien.security.auth.aspect;

import cn.chien.security.auth.handler.AuthMethodHandler;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.ServiceLoader;

/**
 * @author qiandq3
 * @date 2022/11/28
 */
public class AnnotationAuthorizingMethodInterceptor implements MethodInterceptor {
    
    private final ServiceLoader<AuthMethodHandler> authMethodHandlers;
    
    public AnnotationAuthorizingMethodInterceptor(ServiceLoader<AuthMethodHandler> authMethodHandlers) {
        this.authMethodHandlers = authMethodHandlers;
    }
    
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (authMethodHandlers != null) {
            for (AuthMethodHandler authMethodHandler : authMethodHandlers) {
                if (authMethodHandler.support(invocation)) {
                    authMethodHandler.assertAuthorized(invocation);
                }
            }
        }
        return invocation.proceed();
    }
}
