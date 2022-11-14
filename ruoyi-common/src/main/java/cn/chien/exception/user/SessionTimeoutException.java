package cn.chien.exception.user;

/**
 * @author qiandq3
 * @date 2022/11/14
 */
public class SessionTimeoutException extends UserException {
    
    public SessionTimeoutException() {
        super("user.session.timeout", null);
    }
}
