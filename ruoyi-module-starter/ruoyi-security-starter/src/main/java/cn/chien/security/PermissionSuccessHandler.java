package cn.chien.security;

import cn.chien.constant.UserConstants;
import cn.chien.domain.entity.SysUser;
import cn.chien.security.common.Logins;
import cn.chien.service.ISysMenuService;
import cn.chien.service.ISysRoleService;
import cn.chien.utils.spring.SpringUtils;
import org.springframework.security.core.Authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Set;

/**
 * @author qiandq3
 * @date 2022/11/25
 */
public class PermissionSuccessHandler implements LoginSuccessHandler {
    
    private static final ISysMenuService menuService = SpringUtils.getBean(ISysMenuService.class);
    
    private static final ISysRoleService roleService = SpringUtils.getBean(ISysRoleService.class);
    
    @Override
    public void onLoginSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
        SysUser sysUser = Logins.LOGIN_USER.get();
        HttpSession session = request.getSession(false);
        Set<String> perms = menuService.selectPermsByUserId(sysUser.getUserId());
        session.setAttribute(UserConstants.USER_PERMISSION, perms);
        Set<String> roles = roleService.selectRoleKeys(sysUser.getUserId());
        session.setAttribute(UserConstants.USER_ROLE, roles);
    }
    
}
