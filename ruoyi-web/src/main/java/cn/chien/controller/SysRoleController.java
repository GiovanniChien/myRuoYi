package cn.chien.controller;

import cn.chien.annotation.BusinessLog;
import cn.chien.constant.UserConstants;
import cn.chien.controller.base.BaseController;
import cn.chien.core.auth.AuthThreadLocal;
import cn.chien.core.domain.AjaxResult;
import cn.chien.core.page.TableDataInfo;
import cn.chien.domain.SysUserRole;
import cn.chien.domain.Ztree;
import cn.chien.domain.entity.SysRole;
import cn.chien.domain.entity.SysUser;
import cn.chien.enums.BusinessType;
import cn.chien.poi.ExcelUtil;
import cn.chien.request.RoleListPageQueryRequest;
import cn.chien.request.UserListPageQueryRequest;
import cn.chien.request.UserRoleBinderRequest;
import cn.chien.security.auth.annotation.RequiresPermissions;
import cn.chien.security.util.PrincipalUtil;
import cn.chien.service.ISysDeptService;
import cn.chien.service.ISysRoleService;
import cn.chien.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Controller
@RequestMapping("/system/role")
public class SysRoleController extends BaseController {
    
    private static final String prefix = "system/role";
    
    @Autowired
    private ISysRoleService roleService;
    
    @Autowired
    private ISysDeptService deptService;
    
    @Autowired
    private ISysUserService userService;
    
    @RequiresPermissions("system:role:view")
    @GetMapping()
    public String role() {
        return prefix + "/role";
    }
    
    @RequiresPermissions("system:role:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(@RequestBody RoleListPageQueryRequest roleListPageQueryRequest) {
        return roleService.selectRoleList(roleListPageQueryRequest);
    }
    
    /**
     * 新增角色
     */
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }
    
    /**
     * 新增保存角色
     */
    @RequiresPermissions("system:role:add")
    @BusinessLog(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(@Validated @RequestBody SysRole role) {
        if (UserConstants.ROLE_NAME_NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
            return error("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (UserConstants.ROLE_KEY_NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))) {
            return error("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setCreateBy(AuthThreadLocal.getLoginName());
        PrincipalUtil.forceLogoutByRoleId(role.getRoleId());
        return toAjax(roleService.insertRole(role));
        
    }
    
    /**
     * 校验角色名称
     */
    @PostMapping("/checkRoleNameUnique")
    @ResponseBody
    public String checkRoleNameUnique(SysRole role) {
        return roleService.checkRoleNameUnique(role);
    }
    
    /**
     * 校验角色权限
     */
    @PostMapping("/checkRoleKeyUnique")
    @ResponseBody
    public String checkRoleKeyUnique(SysRole role) {
        return roleService.checkRoleKeyUnique(role);
    }
    
    /**
     * 修改角色
     */
    @RequiresPermissions("system:role:edit")
    @GetMapping("/edit/{roleId}")
    public String edit(@PathVariable("roleId") Long roleId, ModelMap mmap) {
        roleService.checkRoleDataScope(roleId);
        mmap.put("role", roleService.selectRoleById(roleId));
        return prefix + "/edit";
    }
    
    /**
     * 修改保存角色
     */
    @RequiresPermissions("system:role:edit")
    @BusinessLog(title = "角色管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(@Validated @RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        if (UserConstants.ROLE_NAME_NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
            return error("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (UserConstants.ROLE_KEY_NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))) {
            return error("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setUpdateBy(AuthThreadLocal.getLoginName());
        PrincipalUtil.forceLogoutByRoleId(role.getRoleId());
        return toAjax(roleService.updateRole(role));
    }
    
    @RequiresPermissions("system:role:remove")
    @BusinessLog(title = "角色管理", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(roleService.deleteRoleByIds(ids));
    }
    
    /**
     * 角色分配数据权限
     */
    @GetMapping("/authDataScope/{roleId}")
    public String authDataScope(@PathVariable("roleId") Long roleId, ModelMap mmap) {
        mmap.put("role", roleService.selectRoleById(roleId));
        return prefix + "/dataScope";
    }
    
    /**
     * 保存角色分配数据权限
     */
    @RequiresPermissions("system:role:edit")
    @BusinessLog(title = "角色管理", businessType = BusinessType.UPDATE)
    @PostMapping("/authDataScope")
    @ResponseBody
    public AjaxResult authDataScopeSave(@RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        role.setUpdateBy(AuthThreadLocal.getLoginName());
        if (roleService.authDataScope(role) > 0) {
            setSysUser(userService.selectUserById(AuthThreadLocal.getUserId()));
            return success();
        }
        return error();
    }
    
    /**
     * 加载角色部门（数据权限）列表树
     */
    @RequiresPermissions("system:role:edit")
    @GetMapping("/deptTreeData")
    @ResponseBody
    public List<Ztree> deptTreeData(SysRole role) {
        return deptService.roleDeptTreeData(role);
    }
    
    /**
     * 分配用户
     */
    @RequiresPermissions("system:role:edit")
    @GetMapping("/authUser/{roleId}")
    public String authUser(@PathVariable("roleId") Long roleId, ModelMap mmap) {
        mmap.put("role", roleService.selectRoleById(roleId));
        return prefix + "/authUser";
    }
    
    /**
     * 查询已分配用户角色列表
     */
    @RequiresPermissions("system:role:list")
    @PostMapping("/authUser/allocatedList")
    @ResponseBody
    public TableDataInfo allocatedList(@RequestBody UserListPageQueryRequest user) {
        return userService.selectAllocatedList(user);
    }
    
    /**
     * 选择用户
     */
    @GetMapping("/authUser/selectUser/{roleId}")
    public String selectUser(@PathVariable("roleId") Long roleId, ModelMap mmap) {
        mmap.put("role", roleService.selectRoleById(roleId));
        return prefix + "/selectUser";
    }
    
    /**
     * 查询未分配用户角色列表
     */
    @RequiresPermissions("system:role:list")
    @PostMapping("/authUser/unallocatedList")
    @ResponseBody
    public TableDataInfo unallocatedList(@RequestBody UserListPageQueryRequest user) {
        return userService.selectUnallocatedList(user);
    }
    
    /**
     * 批量选择用户授权
     */
    @RequiresPermissions("system:role:edit")
    @BusinessLog(title = "角色管理", businessType = BusinessType.GRANT)
    @PostMapping("/authUser/selectAll")
    @ResponseBody
    public AjaxResult selectAuthUserAll(@RequestBody UserRoleBinderRequest userRoleBinderRequest) {
        roleService.checkRoleDataScope(userRoleBinderRequest.getRoleId());
        return toAjax(
                roleService.insertAuthUsers(userRoleBinderRequest.getRoleId(), userRoleBinderRequest.getUserIds()));
    }
    
    /**
     * 取消授权
     */
    @RequiresPermissions("system:role:edit")
    @BusinessLog(title = "角色管理", businessType = BusinessType.GRANT)
    @PostMapping("/authUser/cancel")
    @ResponseBody
    public AjaxResult cancelAuthUser(SysUserRole userRole) {
        return toAjax(roleService.deleteAuthUser(userRole));
    }
    
    /**
     * 批量取消授权
     */
    @RequiresPermissions("system:role:edit")
    @BusinessLog(title = "角色管理", businessType = BusinessType.GRANT)
    @PostMapping("/authUser/cancelAll")
    @ResponseBody
    public AjaxResult cancelAuthUserAll(Long roleId, String userIds) {
        return toAjax(roleService.deleteAuthUsers(roleId, userIds));
    }
    
    @BusinessLog(title = "角色管理", businessType = BusinessType.EXPORT)
    @RequiresPermissions("system:role:export")
    @PostMapping("/export")
    @ResponseBody
    public void export(@RequestBody RoleListPageQueryRequest role, HttpServletResponse response)
            throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        List<SysRole> list = (List<SysRole>) roleService.selectRoleList(role).getRows();
        ExcelUtil<SysRole> util = new ExcelUtil<>(SysRole.class);
        util.export("角色数据", response, list);
    }
}
