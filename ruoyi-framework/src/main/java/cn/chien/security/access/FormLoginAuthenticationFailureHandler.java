package cn.chien.security.access;

import cn.chien.exception.user.ExceedSessionLimitException;
import cn.chien.exception.user.UserBlockedException;
import cn.chien.exception.user.UserDeleteException;
import cn.chien.exception.user.UserException;
import cn.chien.exception.user.UserNotExistsException;
import cn.chien.exception.user.UserPasswordNotMatchException;
import cn.chien.exception.user.UserPasswordRetryLimitExceedException;
import cn.chien.properties.SecurityProperties;
import cn.chien.security.exception.ExceptionPublisher;
import cn.chien.security.exception.PasswordRetryLimitExceedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author qian.diqi
 * @date 2022/7/7
 */
@Component
public class FormLoginAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private ExceptionPublisher exceptionPublisher;

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        try {
            if (BadCredentialsException.class.isAssignableFrom(exception.getClass())) {
                throw new UserPasswordNotMatchException();
            }
            else if (DisabledException.class.isAssignableFrom(exception.getClass())) {
                throw new UserBlockedException();
            }
            else if (AccountExpiredException.class.isAssignableFrom(exception.getClass())) {
                throw new UserDeleteException();
            }
            else if (SessionAuthenticationException.class.isAssignableFrom(exception.getClass())) {
                // "session超过最大数"
                throw new ExceedSessionLimitException();
            }
            else if (PasswordRetryLimitExceedException.class.isAssignableFrom(exception.getClass())) {
                throw new UserPasswordRetryLimitExceedException(securityProperties.getUser().getPassword().getMaxRetryCount(), securityProperties.getUser().getPassword().getLockTime());
            }
        }
        catch (UserException e) {
            exceptionPublisher.process(e, new HttpRequestResponseHolder(request, response), HttpStatus.OK);
        }
    }

}
