package cn.chien.security.util;

import cn.chien.constant.UserConstants;
import cn.chien.domain.entity.SysUser;
import cn.chien.security.access.SecurityUser;
import cn.chien.service.ISysUserService;
import cn.chien.utils.ServletUtils;
import cn.chien.utils.StringUtils;
import cn.chien.utils.spring.SpringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author qiandq3
 * @date 2022/11/11
 */
public final class PrincipalUtil {
    
    private static final String PART_DIVIDER_TOKEN = ":";
    
    private static final String WILDCARD_TOKEN = "*";
    
    private static final ISysUserService userService = SpringUtils.getBean(ISysUserService.class);
    
    public static boolean hasLogin() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.isAuthenticated();
    }
    
    public static Authentication getAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return null;
        }
        return context.getAuthentication();
    }
    
    public static boolean isPermitted(String permission) {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        if (isAdmin()) {
            return true;
        }
        if (authentication.getPrincipal() instanceof SecurityUser securityUser) {
            Set<String> perms = (Set<String>) ServletUtils.getSession().getAttribute(UserConstants.USER_PERMISSION);
            if (CollectionUtils.isEmpty(perms)) {
                return false;
            }
            for (String perm : perms) {
                if (isPermitted(permission, perm)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static boolean isPermitted(String permission, String perm) {
        List<String> parts = Arrays.asList(permission.split(PART_DIVIDER_TOKEN));
        List<String> otherParts = Arrays.asList(perm.split(PART_DIVIDER_TOKEN));
        if (parts.size() == 0) {
            return true;
        }
        if (otherParts.size() == 0) {
            return false;
        }
        boolean[][] dp = new boolean[parts.size()][otherParts.size()];
        if (WILDCARD_TOKEN.equals(parts.get(0))) {
            for (int i = 0; i < otherParts.size(); i++) {
                dp[0][i] = true;
            }
        }
        if (WILDCARD_TOKEN.equals(otherParts.get(0))) {
            for (int i = 0; i < parts.size(); i++) {
                dp[i][0] = true;
            }
        }
        dp[0][0] = dp[0][0] || parts.get(0).equals(otherParts.get(0));
        for (int i = 1; i < parts.size(); i++) {
            for (int j = 1; j < otherParts.size(); j++) {
                if (WILDCARD_TOKEN.equals(parts.get(i)) && WILDCARD_TOKEN.equals(otherParts.get(j))) {
                    dp[i][j] = dp[i - 1][j - 1] || dp[i][j - 1] || dp[i - 1][j];
                } else if (WILDCARD_TOKEN.equals(parts.get(i))) {
                    dp[i][j] = dp[i - 1][j - 1] || dp[i - 1][j];
                } else if (WILDCARD_TOKEN.equals(otherParts.get(j))) {
                    dp[i][j] = dp[i - 1][j - 1] || dp[i][j - 1];
                } else {
                    dp[i][j] = dp[i - 1][j - 1] && parts.get(i).equals(otherParts.get(j));
                }
            }
        }
        return dp[parts.size() - 1][otherParts.size() - 1];
    }
    
    public static boolean isAdmin() {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof SecurityUser securityUser) {
            if (1L == securityUser.getUserId()) {
                return true;
            }
            Collection<GrantedAuthority> authorities = securityUser.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                if ("admin".equals(authority.getAuthority())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void forceLogoutByRoleId(Long roleId) {
        List<SysUser> sysUsers = userService.selectUsersByRoleId(roleId);
        if (CollectionUtils.isEmpty(sysUsers)) {
            return;
        }
        Set<String> loginNames = sysUsers.stream().map(SysUser::getLoginName).collect(Collectors.toSet());
        for (String loginName : loginNames) {
            forceLogout(loginName);
        }
    }
    
    public static void forceLogout(String loginName) {
        if (StringUtils.isEmpty(loginName)) {
            return;
        }
        SessionRepository<?> sessionRepository = SpringUtils.getBean(SessionRepository.class);
        if (sessionRepository instanceof FindByIndexNameSessionRepository<?> findByIndexNameSessionRepository) {
            Map<String, ?> principalNames = findByIndexNameSessionRepository.findByPrincipalName(loginName);
            for (Map.Entry<String, ?> entry : principalNames.entrySet()) {
                Session session = (Session) entry.getValue();
                sessionRepository.deleteById(session.getId());
            }
        }
    }
    
    public static boolean hasRole(String role) {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof SecurityUser securityUser) {
            Collection<GrantedAuthority> authorities = securityUser.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                if (role.equals(authority.getAuthority())) {
                    return true;
                }
            }
        }
        return false;
    }
}
