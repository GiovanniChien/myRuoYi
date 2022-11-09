package cn.chien.controller;

import cn.chien.controller.base.BaseController;
import cn.chien.core.text.Convert;
import cn.chien.domain.entity.SysMenu;
import cn.chien.domain.entity.SysUser;
import cn.chien.properties.ApplicationProperties;
import cn.chien.service.ISysConfigService;
import cn.chien.service.ISysMenuService;
import cn.chien.utils.DateUtils;
import cn.chien.utils.ServletUtils;
import cn.chien.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
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
    public String main(ModelMap mmap)
    {
        mmap.put("version", applicationProperties.getVersion());
        return "main";
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
