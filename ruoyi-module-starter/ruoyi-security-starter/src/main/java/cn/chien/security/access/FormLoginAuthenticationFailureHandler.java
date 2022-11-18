package cn.chien.security.access;

import cn.chien.constant.Constants;
import cn.chien.domain.entity.SysUser;
import cn.chien.exception.user.ExceedSessionLimitException;
import cn.chien.exception.user.UserBlockedException;
import cn.chien.exception.user.UserDeleteException;
import cn.chien.exception.user.UserException;
import cn.chien.exception.user.UserPasswordNotMatchException;
import cn.chien.exception.user.UserPasswordRetryLimitExceedException;
import cn.chien.security.SecurityProperties;
import cn.chien.security.common.Logins;
import cn.chien.security.exception.ExceptionPublisher;
import cn.chien.security.exception.PasswordRetryLimitExceedException;
import cn.chien.service.ISysLoginInfoService;
import cn.chien.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
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
    
    @Autowired
    private ISysLoginInfoService sysLoginInfoService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        SysUser sysUser = Logins.LOGIN_USER.get();
        try {
            if (BadCredentialsException.class.isAssignableFrom(exception.getClass())) {
                throw new UserPasswordNotMatchException();
            }
            else if (DisabledException.class.isAssignableFrom(exception.getClass())) {
                sysLoginInfoService.recordLoginInfo(sysUser.getLoginName(), Constants.LOGIN_FAIL, MessageUtils.message("user.blocked"), request);
                throw new UserBlockedException();
            }
            else if (AccountExpiredException.class.isAssignableFrom(exception.getClass())) {
                sysLoginInfoService.recordLoginInfo(sysUser.getLoginName(), Constants.LOGIN_FAIL, MessageUtils.message("user.password.delete"), request);
                throw new UserDeleteException();
            }
            else if (SessionAuthenticationException.class.isAssignableFrom(exception.getClass())) {
                // "session超过最大数"
                throw new ExceedSessionLimitException();
            }
            else if (PasswordRetryLimitExceedException.class.isAssignableFrom(exception.getClass())) {
                throw new UserPasswordRetryLimitExceedException(securityProperties.getUser().getPassword().getMaxRetryCount(), securityProperties.getUser().getPassword().getLockTime() / 60);
            }
        }
        catch (UserException e) {
            exceptionPublisher.process(e, new HttpRequestResponseHolder(request, response), HttpStatus.OK);
        }
    }

}
