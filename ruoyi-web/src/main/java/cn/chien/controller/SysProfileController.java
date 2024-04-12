package cn.chien.controller;

import cn.chien.annotation.BusinessLog;
import cn.chien.constant.UserConstants;
import cn.chien.core.controller.BaseController;
import cn.chien.core.auth.AuthThreadLocal;
import cn.chien.core.domain.AjaxResult;
import cn.chien.domain.entity.SysUser;
import cn.chien.enums.BusinessType;
import cn.chien.request.ModifyPasswordRequest;
import cn.chien.request.UserInfoModifyRequest;
import cn.chien.service.ISysUserService;
import cn.chien.utils.DateUtils;
import cn.chien.utils.StringUtils;
import cn.chien.utils.file.FileUtils;
import cn.chien.utils.file.MimeTypeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.Base64;

/**
 * @author qiandq3
 * @date 2022/11/9
 */
@Controller
@RequestMapping("/system/user/profile")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SysProfileController extends BaseController {
    
    private static final String prefix = "system/user/profile";
    
    private final ISysUserService userService;
    
    private final PasswordEncoder passwordEncoder;
    
    @GetMapping
    public String profile(ModelMap mmap) {
        SysUser sysUser = getSysUser();
        mmap.put("user", sysUser);
        mmap.put("roleGroup", userService.selectUserRoleGroup(sysUser.getUserId()));
        mmap.put("postGroup", userService.selectUserPostGroup(sysUser.getUserId()));
        return prefix + "/profile";
    }
    
    @GetMapping("/avatar")
    public String avatar(ModelMap mmap) {
        SysUser sysUser = getSysUser();
        mmap.put("user", userService.selectUserById(sysUser.getUserId()));
        return prefix + "/avatar";
    }
    
    @GetMapping("/resetPwd")
    public String resetPwdView(ModelMap modelMap) {
        modelMap.put("user", userService.selectUserById(AuthThreadLocal.getUserId()));
        return prefix + "/resetPwd";
    }
    
    @PostMapping("/updateAvatar")
    @ResponseBody
    @BusinessLog(title = "个人信息", businessType = BusinessType.UPDATE)
    public AjaxResult updateAvatar(@RequestParam("avatarfile") MultipartFile file) {
        SysUser sysUser = getSysUser();
        try {
            if (!file.isEmpty()) {
                String avatar = FileUtils.upload("avatar", file, MimeTypeUtils.IMAGE_EXTENSION);
                sysUser.setAvatar(avatar);
                if (userService.updateUserInfo(sysUser) > 0) {
                    setSysUser(userService.selectUserById(sysUser.getUserId()));
                    return success();
                }
            }
            return error();
        } catch (Exception e) {
            logger.error("修改头像失败！", e);
            return error(e.getMessage());
        }
    }
    
    /**
     * 修改用户
     */
    @PostMapping("/update")
    @ResponseBody
    @BusinessLog(title = "个人信息", businessType = BusinessType.UPDATE)
    public AjaxResult update(@RequestBody UserInfoModifyRequest userInfoModifyRequest) {
        SysUser currentUser = getSysUser();
        currentUser.setUserName(userInfoModifyRequest.getUserName());
        currentUser.setEmail(userInfoModifyRequest.getEmail());
        currentUser.setPhonenumber(userInfoModifyRequest.getPhonenumber());
        currentUser.setSex(userInfoModifyRequest.getSex());
        if (StringUtils.isNotEmpty(userInfoModifyRequest.getPhonenumber()) && UserConstants.USER_PHONE_NOT_UNIQUE.equals(
                userService.checkPhoneUnique(currentUser))) {
            return error("修改用户'" + currentUser.getLoginName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(userInfoModifyRequest.getEmail()) && UserConstants.USER_EMAIL_NOT_UNIQUE.equals(
                userService.checkEmailUnique(currentUser))) {
            return error("修改用户'" + currentUser.getLoginName() + "'失败，邮箱账号已存在");
        }
        if (userService.updateUserInfo(currentUser) > 0) {
            setSysUser(userService.selectUserById(currentUser.getUserId()));
            return success();
        }
        return error();
    }
    
    @BusinessLog(title = "重置密码", businessType = BusinessType.UPDATE)
    @PostMapping("/resetPwd")
    @ResponseBody
    public AjaxResult resetPwd(@RequestBody @Valid ModifyPasswordRequest modifyPasswordRequest) {
        SysUser user = userService.selectUserById(AuthThreadLocal.getUserId());
        String oldPassword = new String(Base64.getDecoder().decode(modifyPasswordRequest.getOldPassword()));
        String newPassword = new String(Base64.getDecoder().decode(modifyPasswordRequest.getNewPassword()));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return error("修改密码失败，旧密码错误");
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            return error("新密码不能与旧密码相同");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPwdUpdateDate(DateUtils.getNowDate());
        if (userService.resetUserPwd(user) > 0) {
            setSysUser(userService.selectUserById(user.getUserId()));
            return success();
        }
        return error("修改密码异常，请联系管理员");
    }
    
}
