package cn.chien.security.access;

import cn.chien.constant.Constants;
import cn.chien.domain.SysUser;
import cn.chien.security.common.Logins;
import cn.chien.service.ISysLoginInfoService;
import cn.chien.service.ISysUserService;
import cn.chien.utils.DateUtils;
import cn.chien.utils.IpUtils;
import cn.chien.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author qian.diqi
 * @date 2022/7/7
 */
@Component
public class FormLoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private ISysUserService sysUserService;
    
    @Autowired
    private ISysLoginInfoService sysLoginInfoService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        recordLoginInfo(request, Logins.LOGIN_USER.get());
        response.sendRedirect("/");
    }

    private void recordLoginInfo(HttpServletRequest request, SysUser sysUser) {
        SysUser user = new SysUser();
        user.setUserId(sysUser.getUserId());
        user.setLoginIp(IpUtils.getIpAddr(request));
        user.setLoginDate(DateUtils.getNowDate());
        sysUserService.updateUserInfo(user);
        sysLoginInfoService.recordLoginInfo(sysUser.getLoginName(), Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"), request);
    }

}
