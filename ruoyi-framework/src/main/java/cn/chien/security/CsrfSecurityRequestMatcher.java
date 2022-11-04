package cn.chien.security;

import cn.chien.annotation.SkipSessionCheck;
import cn.chien.web.handler.RequestMappingHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qiandq3
 * @date 2022/11/3
 */
public class CsrfSecurityRequestMatcher implements RequestMatcher {
    
    private final RequestMappingHandler requestMappingHandler;
    
    public CsrfSecurityRequestMatcher(RequestMappingHandler requestMappingHandler) {
        this.requestMappingHandler = requestMappingHandler;
    }
    
    @Override
    public boolean matches(HttpServletRequest request) {
        return !requestMappingHandler.hasAnnotation(request, SkipSessionCheck.class, RequestMappingHandler.AnnotationScope.ALL);
    }
    
}
