package cn.chien.controller;

import cn.chien.controller.base.BaseController;
import cn.chien.domain.Ztree;
import cn.chien.domain.entity.SysDept;
import cn.chien.service.ISysDeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author qiandq3
 * @date 2022/11/28
 */
@Controller
@RequestMapping("/system/dept")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SysDeptController extends BaseController {
    
    private final ISysDeptService deptService;
    
    /**
     * 加载部门列表树（排除下级）
     */
    @GetMapping({"/treeData/{excludeId}", "/treeData"})
    @ResponseBody
    public List<Ztree> treeDataExcludeChild(@PathVariable(value = "excludeId", required = false) Long excludeId)
    {
        SysDept dept = new SysDept();
        dept.setExcludeId(excludeId);
        return deptService.selectDeptTreeExcludeChild(dept);
    }
    
}
