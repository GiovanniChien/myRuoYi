package cn.chien.controller;

import cn.chien.controller.base.BaseController;
import cn.chien.core.auth.AuthThreadLocal;
import cn.chien.domain.Ztree;
import cn.chien.domain.entity.SysRole;
import cn.chien.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/system/menu")
public class SysMenuController extends BaseController {
    
    private static final String prefix = "system/menu";
    
    @Autowired
    private ISysMenuService menuService;
    
    /**
     * 加载角色菜单列表树
     */
    @GetMapping("/roleMenuTreeData")
    @ResponseBody
    public List<Ztree> roleMenuTreeData(SysRole role)
    {
        Long userId = AuthThreadLocal.getUserId();
        return menuService.roleMenuTreeData(role, userId);
    }
    
}
