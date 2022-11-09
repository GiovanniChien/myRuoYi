package cn.chien.security;

import cn.chien.core.auth.AuthThreadLocal;
import cn.chien.domain.entity.SysUser;
import cn.chien.security.common.Logins;
import org.springframework.security.core.Authentication;
import org.springframework.util.ReflectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author qiandq3
 * @date 2022/11/8
 */
public class UserInfoSuccessHandler implements LoginSuccessHandler {
    
    @Override
    public void onLoginSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
        SysUser sysUser = Logins.LOGIN_USER.get();
        HttpSession session = request.getSession(false);
        AuthThreadLocal.AuthField[] authFields = AuthThreadLocal.AuthField.values();
        Set<String> fieldNames = Arrays.stream(authFields).map(authField -> authField.fieldName)
                .collect(Collectors.toSet());
        ReflectionUtils.doWithFields(SysUser.class, field -> {
            field.setAccessible(true);
            Object val = field.get(sysUser);
            session.setAttribute(field.getName(), val);
            AuthThreadLocal.set(field.getName(), val);
        }, field -> fieldNames.contains(field.getName()));
        session.setAttribute("sysUser", sysUser);
    }
}
