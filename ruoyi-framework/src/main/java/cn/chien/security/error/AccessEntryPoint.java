package cn.chien.security.error;

import cn.chien.exception.user.SessionTimeoutException;
import cn.chien.security.exception.ExceptionPublisher;
import cn.chien.security.util.PrincipalUtil;
import cn.chien.utils.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author qian.diqi
 * @date 2022/7/5
 */
@Component
public class AccessEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private ExceptionPublisher exceptionPublisher;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if (request.getSession(false) == null || !PrincipalUtil.hasLogin()) {
            if (ServletUtils.isAjaxRequest(request)) {
                exceptionPublisher.process(new SessionTimeoutException(), new HttpRequestResponseHolder(request, response), HttpStatus.UNAUTHORIZED);
                return;
            }
            response.sendRedirect("/login");
        }
        if (ServletUtils.isAjaxRequest(request)) {
            exceptionPublisher.process(authException, new HttpRequestResponseHolder(request, response));
            return;
        }
        response.sendRedirect("/login");
    }

}
