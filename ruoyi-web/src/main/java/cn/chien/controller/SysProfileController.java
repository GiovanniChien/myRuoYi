package cn.chien.controller;

import cn.chien.constant.UserConstants;
import cn.chien.controller.base.BaseController;
import cn.chien.core.domain.AjaxResult;
import cn.chien.domain.entity.SysUser;
import cn.chien.properties.ApplicationProperties;
import cn.chien.service.ISysUserService;
import cn.chien.utils.StringUtils;
import cn.chien.utils.file.FileUtils;
import cn.chien.utils.file.MimeTypeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author qiandq3
 * @date 2022/11/9
 */
@Controller
@RequestMapping("/system/user/profile")
@Log
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SysProfileController extends BaseController {
    
    private static final String prefix = "system/user/profile";
    
    private final ISysUserService userService;
    
    private final ApplicationProperties applicationProperties;
    
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
    
    @PostMapping("/updateAvatar")
    @ResponseBody
    public AjaxResult updateAvatar(@RequestParam("avatarfile") MultipartFile file) {
        SysUser sysUser = getSysUser();
        try {
            if (!file.isEmpty()) {
                String avatar = FileUtils.upload(applicationProperties.getProfile() + "/avatar", file,
                        MimeTypeUtils.IMAGE_EXTENSION);
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
    //    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    @ResponseBody
    public AjaxResult update(SysUser user) {
        SysUser currentUser = getSysUser();
        currentUser.setUserName(user.getUserName());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhonenumber(user.getPhonenumber());
        currentUser.setSex(user.getSex());
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && UserConstants.USER_PHONE_NOT_UNIQUE.equals(
                userService.checkPhoneUnique(currentUser))) {
            return error("修改用户'" + currentUser.getLoginName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && UserConstants.USER_EMAIL_NOT_UNIQUE.equals(
                userService.checkEmailUnique(currentUser))) {
            return error("修改用户'" + currentUser.getLoginName() + "'失败，邮箱账号已存在");
        }
        if (userService.updateUserInfo(currentUser) > 0) {
            setSysUser(userService.selectUserById(currentUser.getUserId()));
            return success();
        }
        return error();
    }
    
}
