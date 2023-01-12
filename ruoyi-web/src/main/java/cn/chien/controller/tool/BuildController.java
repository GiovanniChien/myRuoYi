package cn.chien.controller.tool;

import cn.chien.core.controller.BaseController;
import cn.chien.security.auth.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tool/build")
public class BuildController extends BaseController {
    
    private static final String prefix = "tool/build";
    
    @RequiresPermissions("tool:build:view")
    @GetMapping()
    public String build() {
        return prefix + "/build";
    }
    
}
