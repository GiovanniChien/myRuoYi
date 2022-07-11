package cn.chien.exception.user;

/**
 * @author qian.diqi
 * @date 2022/7/8
 */
public class MultipleLoginException extends UserException {

    public MultipleLoginException() {
        super("session.multi.login", null);
    }
}
