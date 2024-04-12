package cn.chien.security.error;

import cn.chien.exception.user.SessionTimeoutException;
import cn.chien.security.exception.ExceptionPublisher;
import cn.chien.security.util.PrincipalUtil;
import cn.chien.utils.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author qian.diqi
 * @date 2022/7/5
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    
    @Autowired
    private ExceptionPublisher exceptionPublisher;
    
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session == null || !PrincipalUtil.hasLogin()) {
            if (ServletUtils.isAjaxRequest(request)) {
                exceptionPublisher.process(new SessionTimeoutException(),
                        new HttpRequestResponseHolder(request, response), HttpStatus.UNAUTHORIZED);
                return;
            }
            response.sendRedirect("/login");
        }
        if (ServletUtils.isAjaxRequest(request)) {
            exceptionPublisher.process(accessDeniedException, new HttpRequestResponseHolder(request, response));
        } else {
            response.sendRedirect("error/unauth");
        }
    }
    
}
