package cn.chien.security.common;

import cn.chien.domain.entity.SysUser;
import org.springframework.core.NamedInheritableThreadLocal;

/**
 * @author qian.diqi
 * @date 2022/7/8
 */
public class Logins {

    public static final ThreadLocal<SysUser> LOGIN_USER = new NamedInheritableThreadLocal<>("LOGIN_USER");

}
