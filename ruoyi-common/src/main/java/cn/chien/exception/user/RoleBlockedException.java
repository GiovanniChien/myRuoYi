package cn.chien.exception.user;

/**
 * 角色锁定异常类
 * 
 * @author ruoyi
 */
public class RoleBlockedException extends UserException {

    public RoleBlockedException()
    {
        super("role.blocked", null);
    }
}
