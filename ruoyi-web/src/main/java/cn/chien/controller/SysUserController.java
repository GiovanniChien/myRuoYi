package cn.chien.controller;

import cn.chien.domain.SysUser;
import cn.chien.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author qian.diqi
 * @date 2022/7/3
 */
@Controller
public class SysUserController {

    @Autowired
    private ISysUserService sysUserService;

    @GetMapping("sysUser/{loginName}")
    @ResponseBody
    public SysUser getSysUserByLoginName(@PathVariable("loginName") String loginName) {
        return sysUserService.selectUserByLoginName(loginName);
    }

}
