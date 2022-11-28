package cn.chien.security.auth.handler;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author qiandq3
 * @date 2022/11/28
 */
public interface AuthMethodHandler {
    
    Class<? extends Annotation> supportAnnotation();
    
    default void assertAuthorized(MethodInvocation methodInvocation) throws AuthenticationException {
        assertAuthorized(methodInvocation, getAnnotation(methodInvocation));
    }
    
    void assertAuthorized(MethodInvocation methodInvocation, Annotation annotation) throws AuthenticationException;
    
    default boolean support(MethodInvocation invocation) {
        return getAnnotation(invocation) != null;
    }
    
    private Annotation getAnnotation(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        Annotation ann = AnnotationUtils.findAnnotation(method, supportAnnotation());
        if (ann != null) {
            return ann;
        }
    
        Class<?> targetClazz = invocation.getThis().getClass();
        Method m = ClassUtils.getMostSpecificMethod(method, targetClazz);
        ann = AnnotationUtils.findAnnotation(m, supportAnnotation());
        if (ann != null) {
            return ann;
        }
        return AnnotationUtils.findAnnotation(targetClazz, supportAnnotation());
    }
}
