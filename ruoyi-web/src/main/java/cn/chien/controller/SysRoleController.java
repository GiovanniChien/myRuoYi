package cn.chien.controller;

import cn.chien.annotation.BusinessLog;
import cn.chien.constant.UserConstants;
import cn.chien.controller.base.BaseController;
import cn.chien.core.auth.AuthThreadLocal;
import cn.chien.core.domain.AjaxResult;
import cn.chien.core.page.TableDataInfo;
import cn.chien.domain.entity.SysRole;
import cn.chien.enums.BusinessType;
import cn.chien.request.RoleListPageQueryRequest;
import cn.chien.security.auth.annotation.RequiresPermissions;
import cn.chien.security.util.PrincipalUtil;
import cn.chien.service.ISysRoleService;
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

import java.util.List;

@Controller
@RequestMapping("/system/role")
public class SysRoleController extends BaseController {
    
    private static final String prefix = "system/role";
    
    @Autowired
    private ISysRoleService roleService;
    
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
}
