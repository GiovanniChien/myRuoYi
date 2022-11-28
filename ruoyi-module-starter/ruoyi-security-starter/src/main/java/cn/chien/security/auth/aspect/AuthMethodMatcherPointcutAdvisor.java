package cn.chien.security.auth.aspect;

import cn.chien.security.auth.handler.AuthMethodHandler;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author qiandq3
 * @date 2022/11/28
 */
public class AuthMethodMatcherPointcutAdvisor extends StaticMethodMatcherPointcutAdvisor {
    
    private ServiceLoader<AuthMethodHandler> authMethodHandlers;
    
    private List<Class<? extends Annotation>> authSupportAnnotationClass;
    
    public AuthMethodMatcherPointcutAdvisor() {
        afterPropertiesSet();
        setAdvice(new AnnotationAuthorizingMethodInterceptor(authMethodHandlers));
    }
    
    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        if (isAuthAnnotationPresent(method)) {
            return true;
        }
    
        // 方法有可能是实现类，注解标注在了父类上，查看接口定义的方法或者父类方法上是否标注了对应注解
        Method m = ClassUtils.getMostSpecificMethod(method, targetClass);
        if (isAuthAnnotationPresent(m)) {
            return true;
        }
        
        return isAuthAnnotationPresent(targetClass);
    }
    
    private boolean isAuthAnnotationPresent(Class<?> targetClass) {
        for (Class<? extends Annotation> annClass : authSupportAnnotationClass) {
            Annotation annotation = AnnotationUtils.findAnnotation(targetClass, annClass);
            if (annotation != null) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isAuthAnnotationPresent(Method method) {
        for (Class<? extends Annotation> annClass : authSupportAnnotationClass) {
            Annotation annotation = AnnotationUtils.findAnnotation(method, annClass);
            if (annotation != null) {
                return true;
            }
        }
        return false;
    }
    
    public void afterPropertiesSet() {
        authMethodHandlers = ServiceLoader.load(AuthMethodHandler.class);
        authSupportAnnotationClass = new ArrayList<>();
        for (AuthMethodHandler authMethodHandler : authMethodHandlers) {
            authSupportAnnotationClass.add(authMethodHandler.supportAnnotation());
        }
    }
}
