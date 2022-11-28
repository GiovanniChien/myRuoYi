package cn.chien.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author qiandq3
 * @date 2022/11/28
 */
public class UnAuthorizedException extends AuthenticationException {
    
    public UnAuthorizedException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    public UnAuthorizedException(String msg) {
        super(msg);
    }
}
