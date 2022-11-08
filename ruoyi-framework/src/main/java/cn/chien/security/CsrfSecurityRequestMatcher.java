package cn.chien.security;

import cn.chien.annotation.SkipSessionCheck;
import cn.chien.web.handler.RequestMappingHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @author qiandq3
 * @date 2022/11/3
 */
public class CsrfSecurityRequestMatcher implements RequestMatcher {
    
    private final RequestMappingHandler requestMappingHandler;
    
    private final HashSet<String> allowedMethods = new HashSet<>(Arrays.asList("GET", "HEAD", "TRACE", "OPTIONS"));
    
    public CsrfSecurityRequestMatcher(RequestMappingHandler requestMappingHandler) {
        this.requestMappingHandler = requestMappingHandler;
    }
    
    @Override
    public boolean matches(HttpServletRequest request) {
        return !allowedMethods.contains(request.getMethod()) &&
                !requestMappingHandler.hasAnnotation(request, SkipSessionCheck.class, RequestMappingHandler.AnnotationScope.ALL);
    }
    
}
