package cn.chien.controller;

import cn.chien.core.controller.BaseController;
import cn.chien.core.domain.AjaxResult;
import cn.chien.domain.entity.SysDept;
import cn.chien.service.ISysDeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author qiandq3
 * @date 2022/11/28
 */
@Controller
@RequestMapping("/system/dept")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SysDeptController extends BaseController {
    
    private static final String prefix = "system/dept";
    
    private final ISysDeptService deptService;
    
    /**
     * 加载部门列表树（排除下级）
     */
    @GetMapping({"/treeData/{excludeId}", "/treeData"})
    @ResponseBody
    public AjaxResult treeDataExcludeChild(@PathVariable(value = "excludeId", required = false) Long excludeId) {
        SysDept dept = new SysDept();
        dept.setExcludeId(excludeId);
        return AjaxResult.success(deptService.selectDeptTreeExcludeChild(dept));
    }
    
    /**
     * 选择部门树
     *
     * @param deptId    部门ID
     * @param excludeId 排除ID
     */
    @GetMapping(value = {"/selectDeptTree/{deptId}", "/selectDeptTree/{deptId}/{excludeId}"})
    public String selectDeptTree(@PathVariable("deptId") Long deptId,
            @PathVariable(value = "excludeId", required = false) Long excludeId, ModelMap mmap) {
        mmap.put("dept", deptService.selectDeptById(deptId));
        mmap.put("excludeId", excludeId);
        return prefix + "/tree";
    }
    
}
