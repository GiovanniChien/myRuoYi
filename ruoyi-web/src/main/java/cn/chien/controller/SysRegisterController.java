package cn.chien.controller;

import cn.chien.annotation.SkipSessionCheck;
import cn.chien.core.controller.BaseController;
import cn.chien.core.domain.AjaxResult;
import cn.chien.domain.entity.SysUser;
import cn.chien.service.ISysConfigService;
import cn.chien.service.ISysRegisterService;
import cn.chien.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author qiandq3
 * @date 2022/11/3
 */
@Controller
@SkipSessionCheck
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class SysRegisterController extends BaseController {
    
    private final ISysRegisterService registerService;
    
    private final ISysConfigService configService;
    
    
    @GetMapping("/register")
    public String register(ModelMap mMap) {
        mMap.put("captchaEnabled", true);
        return "register";
    }
    
    @PostMapping("/register")
    @ResponseBody
    public AjaxResult ajaxRegister(SysUser user) {
        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser")))) {
            return error("当前系统没有开启注册功能！");
        }
        String msg = registerService.register(user);
        return StringUtils.isEmpty(msg) ? success() : error(msg);
    }
    
}
