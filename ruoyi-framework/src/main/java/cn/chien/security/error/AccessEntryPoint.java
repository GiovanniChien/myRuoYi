package cn.chien.security.error;

import cn.chien.core.domain.AjaxResult;
import cn.chien.security.exception.ExceptionPublisher;
import cn.chien.utils.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

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
        if (ServletUtils.isAjaxRequest(request)) {
            exceptionPublisher.process(authException, new HttpRequestResponseHolder(request, response));
            return;
        }
        response.sendRedirect("/login");
    }

}
