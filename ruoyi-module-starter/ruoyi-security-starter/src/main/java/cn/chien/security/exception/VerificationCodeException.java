package cn.chien.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author qian.diqi
 * @date 2022/7/6
 */
public class VerificationCodeException extends AuthenticationException {

    public VerificationCodeException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public VerificationCodeException(String msg) {
        super(msg);
    }

}
