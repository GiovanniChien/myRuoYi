package cn.chien.controller.tool;

import cn.chien.core.controller.BaseController;
import cn.chien.security.auth.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * swagger 接口
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/tool/swagger")
public class SwaggerController extends BaseController {
    
    @RequiresPermissions("tool:swagger:view")
    @GetMapping()
    public String index() {
        return redirect("/swagger-ui/index.html");
    }
}
