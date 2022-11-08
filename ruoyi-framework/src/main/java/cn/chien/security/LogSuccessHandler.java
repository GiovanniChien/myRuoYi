package cn.chien.security;

import cn.chien.constant.Constants;
import cn.chien.domain.SysUser;
import cn.chien.security.common.Logins;
import cn.chien.service.ISysLoginInfoService;
import cn.chien.service.ISysUserService;
import cn.chien.utils.DateUtils;
import cn.chien.utils.IpUtils;
import cn.chien.utils.MessageUtils;
import cn.chien.utils.spring.SpringUtils;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author qiandq3
 * @date 2022/11/8
 */
public class LogSuccessHandler implements LoginSuccessHandler {
    
    private static final ISysUserService sysUserService = SpringUtils.getBean(ISysUserService.class);
    
    private static final ISysLoginInfoService sysLoginInfoService = SpringUtils.getBean(ISysLoginInfoService.class);
    
    @Override
    public void onLoginSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
        SysUser sysUser = Logins.LOGIN_USER.get();
        SysUser user = new SysUser();
        user.setUserId(sysUser.getUserId());
        user.setLoginIp(IpUtils.getIpAddr(request));
        user.setLoginDate(DateUtils.getNowDate());
        sysUserService.updateUserInfo(user);
        sysLoginInfoService.recordLoginInfo(sysUser.getLoginName(), Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"), request);
    }
}
