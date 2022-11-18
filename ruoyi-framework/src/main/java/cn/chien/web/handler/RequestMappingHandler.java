package cn.chien.web.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.server.RequestPath;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.ServletRequestPathUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author qian.diqi
 * @date 2022/7/6
 */
@Component
public class RequestMappingHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestMappingHandler.class);

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    public <A extends Annotation> boolean hasAnnotation(HttpServletRequest request, Class<A> annotationClazz, AnnotationScope annotationScope) {
        Object methodHandler;
        try {
            if (!ServletRequestPathUtils.hasParsedRequestPath(request)) {
                ServletRequestPathUtils.parseAndCache(request);
            }
            HandlerExecutionChain hec = requestMappingHandlerMapping.getHandler(request);
            if (hec == null) {
                return false;
            }
            methodHandler = hec.getHandler();
        }
        catch (Exception e) {
            logger.error("获取请求的handler失败", e);
            return false;
        }
        if (HandlerMethod.class.isAssignableFrom(methodHandler.getClass())) {
            HandlerMethod method = (HandlerMethod) methodHandler;
            return containsAnnotation(method, annotationClazz, annotationScope);
        }
        return false;
    }

    private <A extends Annotation> boolean containsAnnotation(HandlerMethod method, Class<A> annotationClazz, AnnotationScope annotationScope) {
        boolean contains = false;
        switch (annotationScope) {
            case ALL -> contains = methodContainsAnnotation(method.getMethod(), annotationClazz)
                    || classContainsAnnotation(method.getMethod().getDeclaringClass(), annotationClazz);
            case CLASS -> contains = classContainsAnnotation(method.getMethod().getDeclaringClass(), annotationClazz);
            case METHOD -> contains = methodContainsAnnotation(method.getMethod(), annotationClazz);
        }
        return contains;
    }

    private <A extends Annotation> boolean classContainsAnnotation(Class<?> clazz, Class<A> annotationClazz) {
        A annotation = clazz.getDeclaredAnnotation(annotationClazz);
        return annotation != null;
    }

    private <A extends Annotation> boolean methodContainsAnnotation(Method method, Class<A> annotationClazz) {
        A annotation = method.getDeclaredAnnotation(annotationClazz);
        return annotation != null;
    }

    public enum AnnotationScope {
        ALL,
        CLASS,
        METHOD
    }

}
