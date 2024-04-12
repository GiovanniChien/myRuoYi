package cn.chien.service.impl;

import cn.chien.constant.Constants;
import cn.chien.constant.UserConstants;
import cn.chien.domain.entity.SysUser;
import cn.chien.service.ISysLoginInfoService;
import cn.chien.service.ISysRegisterService;
import cn.chien.service.ISysUserService;
import cn.chien.utils.DateUtils;
import cn.chien.utils.MessageUtils;
import cn.chien.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author qiandq3
 * @date 2022/11/3
 */
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SysRegisterServiceImpl implements ISysRegisterService {
    
    private final ISysUserService userService;
    
    private final PasswordEncoder passwordEncoder;
    
    private final ISysLoginInfoService sysLoginInfoService;
    
    @Override
    public String register(SysUser user) {
        String msg = "", loginName = user.getLoginName(), password = user.getPassword();
        
        if (StringUtils.isEmpty(loginName)) {
            msg = "用户名不能为空";
        } else if (StringUtils.isEmpty(password)) {
            msg = "用户密码不能为空";
        } else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            msg = "密码长度必须在5到20个字符之间";
        } else if (loginName.length() < UserConstants.USERNAME_MIN_LENGTH
                || loginName.length() > UserConstants.USERNAME_MAX_LENGTH) {
            msg = "账户长度必须在2到20个字符之间";
        } else if (UserConstants.USER_NAME_NOT_UNIQUE.equals(userService.checkLoginNameUnique(loginName))) {
            msg = "保存用户'" + loginName + "'失败，注册账号已存在";
        } else {
            user.setPwdUpdateDate(DateUtils.getNowDate());
            user.setUserName(loginName);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            boolean regFlag = userService.registerUser(user);
            if (!regFlag) {
                msg = "注册失败,请联系系统管理人员";
            } else {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                sysLoginInfoService.recordLoginInfo(loginName, Constants.REGISTER, MessageUtils.message("user.register.success"), request);
            }
        }
        return msg;
    }
}
