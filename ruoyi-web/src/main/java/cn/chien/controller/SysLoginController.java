package cn.chien.controller;

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
public class SysLoginController {

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
//        mmap.put("captchaEnabled", true);
        return "login";
    }

}
