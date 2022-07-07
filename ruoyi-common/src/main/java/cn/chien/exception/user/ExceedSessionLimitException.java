package cn.chien.exception.user;

/**
 * @author qian.diqi
 * @date 2022/7/7
 */
public class ExceedSessionLimitException extends UserException {

    public ExceedSessionLimitException() {
        super("session.exceed.limit", null);
    }
}
