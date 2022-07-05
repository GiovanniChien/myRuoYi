package cn.chien.security.access;

import cn.chien.constant.UserConstants;
import cn.chien.domain.SysRole;
import cn.chien.domain.SysUser;
import cn.chien.service.ISysConfigService;
import cn.chien.service.ISysUserService;
import cn.chien.utils.DateUtils;
import cn.hutool.core.convert.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author qian.diqi
 * @date 2022/7/4
 */
@Component
public class SysUserDetailsService implements UserDetailsService {

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysConfigService sysConfigService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserService.selectUserByLoginName(username);
        return createUser(sysUser);
    }

    private User createUser(SysUser sysUser) {
        boolean enabled = UserConstants.NORMAL.equals(sysUser.getStatus());
        boolean credentialsNonExpired = !checkAccountExpired(sysUser.getPwdUpdateDate());
        List<SysRole> roles = sysUser.getRoles();
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (SysRole role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getRoleKey()));
        }
        return new User(sysUser.getLoginName(), sysUser.getPassword(), enabled, true, credentialsNonExpired, true, authorities);
    }

    private boolean checkAccountExpired(Date pwdUpdateDate) {
        Integer passwordValidateDays = Convert.toInt(sysConfigService.selectConfigByKey("sys.account.passwordValidateDays"));
        if (passwordValidateDays != null && passwordValidateDays > 0) {
            if (pwdUpdateDate == null) {
                // 如果从未修改过初始密码，直接提醒过期
                return true;
            }
            return DateUtils.differentDaysByMillisecond(DateUtils.getNowDate(), pwdUpdateDate) > passwordValidateDays;
        }
        return false;
    }

}
