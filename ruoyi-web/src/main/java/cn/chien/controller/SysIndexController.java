package cn.chien.controller;

import cn.chien.core.controller.BaseController;
import cn.chien.core.auth.AuthThreadLocal;
import cn.chien.core.domain.AjaxResult;
import cn.chien.core.text.Convert;
import cn.chien.domain.entity.SysMenu;
import cn.chien.domain.entity.SysUser;
import cn.chien.properties.ApplicationProperties;
import cn.chien.service.ISysConfigService;
import cn.chien.service.ISysMenuService;
import cn.chien.service.ISysUserService;
import cn.chien.utils.DateUtils;
import cn.chien.utils.ServletUtils;
import cn.chien.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * @author qiandq3
 * @date 2022/11/9
 */
@Controller
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SysIndexController extends BaseController {
    
    private final ISysMenuService menuService;
    
    private final ISysConfigService configService;
    
    private final ApplicationProperties applicationProperties;
    
    private final PasswordEncoder passwordEncoder;
    
    private final ISysUserService sysUserService;
    
    @GetMapping("/index")
    public String index(ModelMap mmap, HttpSession session) {
        SysUser sysUser = (SysUser) session.getAttribute("sysUser");
        // 根据用户id取出菜单
        List<SysMenu> menus = menuService.selectMenusByUser(sysUser);
        mmap.put("menus", menus);
        mmap.put("user", sysUser);
        mmap.put("sideTheme", configService.selectConfigByKey("sys.index.sideTheme"));
        mmap.put("skinName", configService.selectConfigByKey("sys.index.skinName"));
        Boolean footer = Convert.toBool(configService.selectConfigByKey("sys.index.footer"), true);
        Boolean tagsView = Convert.toBool(configService.selectConfigByKey("sys.index.tagsView"), true);
        mmap.put("footer", footer);
        mmap.put("tagsView", tagsView);
        mmap.put("mainClass", contentMainClass(footer, tagsView));
        mmap.put("copyrightYear", applicationProperties.getCopyrightYear());
        mmap.put("demoEnabled", applicationProperties.isDemoEnabled());
        mmap.put("isDefaultModifyPwd", initPasswordIsModify(sysUser.getPwdUpdateDate()));
        mmap.put("isPasswordExpired", passwordIsExpiration(sysUser.getPwdUpdateDate()));
        mmap.put("isMobile", ServletUtils.checkAgentIsMobile(ServletUtils.getRequest().getHeader("User-Agent")));
        
        // 菜单导航显示风格
        String menuStyle = configService.selectConfigByKey("sys.index.menuStyle");
        // 移动端，默认使左侧导航菜单，否则取默认配置
        String indexStyle = ServletUtils.checkAgentIsMobile(ServletUtils.getRequest().getHeader("User-Agent")) ? "index"
                : menuStyle;
        
        // 优先Cookie配置导航菜单
        Cookie[] cookies = ServletUtils.getRequest().getCookies();
        for (Cookie cookie : cookies) {
            if (StringUtils.isNotEmpty(cookie.getName()) && "nav-style".equalsIgnoreCase(cookie.getName())) {
                indexStyle = cookie.getValue();
                break;
            }
        }
        return "topnav".equalsIgnoreCase(indexStyle) ? "index-topnav" : "index";
    }
    
    @GetMapping("/system/main")
    public String main(ModelMap mmap) {
        mmap.put("version", applicationProperties.getVersion());
        return "main";
    }
    
    @GetMapping("/system/switchSkin")
    public String switchSkin() {
        return "skin";
    }
    
    @GetMapping("/system/menuStyle/{style}")
    public void menuStyle(@PathVariable String style, HttpServletResponse response) {
        Cookie cookie = new Cookie("nav-style", style);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
    
    @GetMapping("/lockscreen")
    public String lockscreen(ModelMap mmap, HttpSession httpSession) {
        mmap.put("user", getSysUser());
        httpSession.setAttribute("lockscreen", true);
        return "lock";
    }
    
    @PostMapping("/unlockscreen")
    @ResponseBody
    public AjaxResult unlockscreen(String password) {
        password = new String(Base64.getDecoder().decode(password));
        SysUser user = sysUserService.selectUserById(AuthThreadLocal.getUserId());
        if (passwordEncoder.matches(password, user.getPassword())) {
            ServletUtils.getSession().removeAttribute("lockscreen");
            return AjaxResult.success();
        }
        return AjaxResult.error("密码不正确，请重新输入。");
    }
    
    public String contentMainClass(Boolean footer, Boolean tagsView) {
        if (!footer && !tagsView) {
            return "tagsview-footer-hide";
        } else if (!footer) {
            return "footer-hide";
        } else if (!tagsView) {
            return "tagsview-hide";
        }
        return StringUtils.EMPTY;
    }
    
    // 检查初始密码是否提醒修改
    public boolean initPasswordIsModify(Date pwdUpdateDate) {
        Integer initPasswordModify = Convert.toInt(configService.selectConfigByKey("sys.account.initPasswordModify"));
        return initPasswordModify != null && initPasswordModify == 1 && pwdUpdateDate == null;
    }
    
    // 检查密码是否过期
    public boolean passwordIsExpiration(Date pwdUpdateDate) {
        Integer passwordValidateDays = Convert.toInt(
                configService.selectConfigByKey("sys.account.passwordValidateDays"));
        if (passwordValidateDays != null && passwordValidateDays > 0) {
            if (StringUtils.isNull(pwdUpdateDate)) {
                // 如果从未修改过初始密码，直接提醒过期
                return true;
            }
            Date nowDate = DateUtils.getNowDate();
            return DateUtils.differentDaysByMillisecond(nowDate, pwdUpdateDate) > passwordValidateDays;
        }
        return false;
    }
    
}
