package cn.chien.controller;

import cn.chien.annotation.SkipSessionCheck;
import cn.chien.security.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author qian.diqi
 * @date 2022/7/2
 */
@Controller
@SkipSessionCheck
public class SysLoginController {

    @Autowired
    private SecurityProperties securityProperties;

    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response, ModelMap mmap)
    {
//        // 如果是Ajax请求，返回Json字符串。
//        if (ServletUtils.isAjaxRequest(request))
//        {
//            return ServletUtils.renderString(response, "{\"code\":\"1\",\"msg\":\"未登录或登录超时。请重新登录\"}");
//        }
        // 是否开启记住我
        mmap.put("isRemembered", false);
        // 是否开启用户注册
        mmap.put("isAllowRegister", true);
        mmap.put("captchaEnabled", securityProperties.getUser().getCaptchaEnabled());
        return "login";
    }

}
