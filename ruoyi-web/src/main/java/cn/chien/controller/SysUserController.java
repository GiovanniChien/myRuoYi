package cn.chien.controller;

import cn.chien.controller.base.BaseController;
import cn.chien.core.domain.AjaxResult;
import cn.chien.core.page.TableDataInfo;
import cn.chien.domain.entity.SysDept;
import cn.chien.domain.entity.SysUser;
import cn.chien.request.UserListPageQueryRequest;
import cn.chien.security.auth.annotation.RequiresPermissions;
import cn.chien.service.ISysDeptService;
import cn.chien.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author qian.diqi
 * @date 2022/7/3
 */
@Controller
@RequestMapping("/system/user")
public class SysUserController extends BaseController {
    
    private final static String prefix = "system/user";
    
    @Autowired
    private ISysUserService sysUserService;
    
    @Autowired
    private ISysDeptService deptService;
    
    //    @GetMapping("sysUser/{loginName}")
    //    @ResponseBody
    //    public SysUser getSysUserByLoginName(@PathVariable("loginName") String loginName) {
    //        return sysUserService.selectUserByLoginName(loginName);
    //    }
    
    /**
     * 校验手机号码
     */
    @PostMapping("checkPhoneUnique")
    @ResponseBody
    public String checkPhoneUnique(SysUser user) {
        return sysUserService.checkPhoneUnique(user);
    }
    
    @PostMapping("checkEmailUnique")
    @ResponseBody
    public String checkEmailUnique(SysUser user) {
        return sysUserService.checkEmailUnique(user);
    }
    
    @RequiresPermissions("system:user:view")
    @GetMapping()
    public String user() {
        return prefix + "/user";
    }
    
    @PostMapping("/list")
    @RequiresPermissions("system:user:list")
    @ResponseBody
    public TableDataInfo list(@RequestBody UserListPageQueryRequest user) {
        return sysUserService.selectUserList(user);
    }
    
    @RequiresPermissions("system:user:list")
    @GetMapping("/deptTreeData")
    @ResponseBody
    public AjaxResult deptTreeData() {
        return AjaxResult.success(deptService.selectDeptTree(new SysDept()));
    }
}
