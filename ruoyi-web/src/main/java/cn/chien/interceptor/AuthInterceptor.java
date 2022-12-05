package cn.chien.interceptor;

import cn.chien.core.auth.AuthThreadLocal;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author qiandq3
 * @date 2022/11/8
 */
public class AuthInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(AuthThreadLocal.AuthField.USER_ID.fieldName) == null) {
            return true;
        }
        AuthThreadLocal.AuthField[] authFields = AuthThreadLocal.AuthField.values();
        for (AuthThreadLocal.AuthField authField : authFields) {
            AuthThreadLocal.set(authField.fieldName, session.getAttribute(authField.fieldName));
        }
        AuthThreadLocal.set("sysUser", session.getAttribute("sysUser"));
        return true;
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        AuthThreadLocal.remove();
    }
}
