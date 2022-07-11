package cn.chien.security.error;

import cn.chien.security.exception.ExceptionPublisher;
import cn.chien.utils.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
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
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    private ExceptionPublisher exceptionPublisher;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if (ServletUtils.isAjaxRequest(request)) {
            exceptionPublisher.process(accessDeniedException, new HttpRequestResponseHolder(request, response));
        }
        else {
            response.sendRedirect("error/unauth");
        }
    }

}
