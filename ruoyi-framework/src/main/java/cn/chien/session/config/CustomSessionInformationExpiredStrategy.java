package cn.chien.session.config;

import cn.chien.exception.user.MultipleLoginException;
import cn.chien.security.exception.ExceptionPublisher;
import cn.chien.utils.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author qian.diqi
 * @date 2022/7/8
 */
@Component
public class CustomSessionInformationExpiredStrategy implements SessionInformationExpiredStrategy {
    
    @Autowired
    private ExceptionPublisher exceptionPublisher;
    
    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        if (ServletUtils.isAjaxRequest(event.getRequest())) {
            exceptionPublisher.process(new MultipleLoginException(),
                    new HttpRequestResponseHolder(event.getRequest(), event.getResponse()), HttpStatus.UNAUTHORIZED);
        } else {
            event.getResponse().sendRedirect("/login?kickout=1");
        }
    }
    
}
