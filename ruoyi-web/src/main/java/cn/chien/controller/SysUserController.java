package cn.chien.controller;

import cn.chien.annotation.BusinessLog;
import cn.chien.constant.UserConstants;
import cn.chien.core.controller.BaseController;
import cn.chien.core.auth.AuthThreadLocal;
import cn.chien.core.domain.AjaxResult;
import cn.chien.core.page.TableDataInfo;
import cn.chien.core.text.Convert;
import cn.chien.domain.Ztree;
import cn.chien.domain.entity.SysDept;
import cn.chien.domain.entity.SysRole;
import cn.chien.domain.entity.SysUser;
import cn.chien.enums.BusinessType;
import cn.chien.request.UserListPageQueryRequest;
import cn.chien.request.UserRoleRequest;
import cn.chien.security.auth.annotation.RequiresPermissions;
import cn.chien.security.util.PrincipalUtil;
import cn.chien.service.ISysConfigService;
import cn.chien.service.ISysDeptService;
import cn.chien.service.ISysPostService;
import cn.chien.service.ISysRoleService;
import cn.chien.service.ISysUserService;
import cn.chien.utils.StringUtils;
import cn.chien.poi.ExcelUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

import static cn.chien.core.auth.AuthThreadLocal.getLoginName;

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
    
    @Autowired
    private ISysRoleService roleService;
    
    @Autowired
    private ISysPostService postService;
    
    @Autowired
    private ISysConfigService configService;
    
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
    
    @PostMapping("/checkLoginNameUnique")
    @ResponseBody
    public String checkLoginNameUnique(SysUser user) {
        return sysUserService.checkLoginNameUnique(user.getLoginName());
    }
    
    @RequiresPermissions("system:user:view")
    @GetMapping()
    public String user() {
        return prefix + "/user";
    }
    
    @GetMapping("/add")
    public String add(ModelMap mmap) {
        mmap.put("roles", roleService.selectRoleAll().stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        mmap.put("posts", postService.selectPostAll());
        return prefix + "/add";
    }
    
    /**
     * 新增保存用户
     */
    @RequiresPermissions("system:user:add")
    @BusinessLog(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(@RequestBody @Validated SysUser user) {
        if (UserConstants.USER_NAME_NOT_UNIQUE.equals(sysUserService.checkLoginNameUnique(user.getLoginName()))) {
            return error("新增用户'" + user.getLoginName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && UserConstants.USER_PHONE_NOT_UNIQUE.equals(
                sysUserService.checkPhoneUnique(user))) {
            return error("新增用户'" + user.getLoginName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && UserConstants.USER_EMAIL_NOT_UNIQUE.equals(
                sysUserService.checkEmailUnique(user))) {
            return error("新增用户'" + user.getLoginName() + "'失败，邮箱账号已存在");
        }
        user.setPassword(configService.selectConfigByKey("sys.user.initPassword"));
        user.setCreateBy(getLoginName());
        return toAjax(sysUserService.insertUser(user));
    }
    
    /**
     * 修改用户
     */
    @RequiresPermissions("system:user:edit")
    @GetMapping("/edit/{userId}")
    public String edit(@PathVariable("userId") Long userId, ModelMap mmap) {
        sysUserService.checkUserDataScope(userId);
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        mmap.put("user", sysUserService.selectUserById(userId));
        mmap.put("roles", SysUser.isAdmin(userId) ? roles
                : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        mmap.put("posts", postService.selectPostsByUserId(userId));
        return prefix + "/edit";
    }
    
    @RequiresPermissions("system:user:edit")
    @BusinessLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(@RequestBody @Validated SysUser user) {
        sysUserService.checkUserAllowed(user);
        sysUserService.checkUserDataScope(user.getUserId());
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && UserConstants.USER_PHONE_NOT_UNIQUE.equals(
                sysUserService.checkPhoneUnique(user))) {
            return error("修改用户'" + user.getLoginName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && UserConstants.USER_EMAIL_NOT_UNIQUE.equals(
                sysUserService.checkEmailUnique(user))) {
            return error("修改用户'" + user.getLoginName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(getLoginName());
        PrincipalUtil.forceLogout(user.getLoginName());
        return toAjax(sysUserService.updateUser(user));
    }
    
    @RequiresPermissions("system:user:remove")
    @BusinessLog(title = "用户管理", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(@RequestParam("ids") String idStr) {
        Long[] ids = Convert.toLongArray(idStr);
        if (ArrayUtils.contains(ids, AuthThreadLocal.getUserId())) {
            return error("当前用户不能删除");
        }
        return toAjax(sysUserService.deleteUserByIds(ids));
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
    public List<Ztree> deptTreeData() {
        return deptService.selectDeptTree(new SysDept());
    }
    
    @RequiresPermissions("system:user:list")
    @GetMapping("/selectDeptTree/{deptId}")
    public String selectDeptTree(@PathVariable("deptId") Long deptId, ModelMap mmap) {
        mmap.put("dept", deptService.selectDeptById(deptId));
        return prefix + "/deptTree";
    }
    
    @RequiresPermissions("system:user:resetPwd")
    @PutMapping("/resetPwd/{userId}")
    @ResponseBody
    public AjaxResult resetPwd(@PathVariable("userId") Long userId) {
        SysUser user = new SysUser(userId);
        sysUserService.checkUserAllowed(user);
        sysUserService.checkUserDataScope(userId);
        user.setPassword(configService.selectConfigByKey("sys.user.initPassword"));
        if (sysUserService.resetUserPwd(user) > 0) {
            PrincipalUtil.forceLogout(sysUserService.selectUserById(userId).getLoginName());
            return success();
        }
        return error();
    }
    
    /**
     * 进入授权角色页
     */
    @GetMapping("/authRole/{userId}")
    public String authRole(@PathVariable("userId") Long userId, ModelMap mmap) {
        SysUser user = sysUserService.selectUserById(userId);
        // 获取用户所属的角色列表
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        mmap.put("user", user);
        mmap.put("roles", SysUser.isAdmin(userId) ? roles
                : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        return prefix + "/authRole";
    }
    
    /**
     * 用户授权角色
     */
    @RequiresPermissions("system:user:edit")
    @BusinessLog(title = "用户管理", businessType = BusinessType.GRANT)
    @PostMapping("/authRole/insertAuthRole")
    @ResponseBody
    public AjaxResult insertAuthRole(@RequestBody UserRoleRequest userRoleRequest) {
        sysUserService.checkUserDataScope(userRoleRequest.getUserId());
        sysUserService.insertUserAuth(userRoleRequest.getUserId(), userRoleRequest.getRoleIds());
        PrincipalUtil.forceLogout(sysUserService.selectUserById(userRoleRequest.getUserId()).getLoginName());
        return success();
    }
    
    @RequiresPermissions("system:user:view")
    @GetMapping(value = "/importTemplate")
    @ResponseBody
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<SysUser> util = new ExcelUtil<>(SysUser.class);
        util.importTemplateExcel("用户数据", response);
    }
    
    @BusinessLog(title = "用户管理", businessType = BusinessType.IMPORT)
    @RequiresPermissions("system:user:import")
    @PostMapping("/importData")
    @ResponseBody
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<SysUser> util = new ExcelUtil<>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        String message = sysUserService.importUser(userList, updateSupport, getLoginName());
        return AjaxResult.success(message);
    }
    
    @BusinessLog(title = "用户管理", businessType = BusinessType.EXPORT)
    @RequiresPermissions("system:user:export")
    @PostMapping("/export")
    @ResponseBody
    public void export(@RequestBody UserListPageQueryRequest user, HttpServletResponse response)
            throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        List<SysUser> list = (List<SysUser>) sysUserService.selectUserList(user).getRows();
        ExcelUtil<SysUser> util = new ExcelUtil<>(SysUser.class);
        util.export( "用户数据", response, list);
    }
    
}
