package cn.chien.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author qian.diqi
 * @date 2022/7/7
 */
public class PasswordRetryLimitExceedException extends AuthenticationException {

    public PasswordRetryLimitExceedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public PasswordRetryLimitExceedException(String msg) {
        super(msg);
    }
}
