package cn.chien.security.auth.aspect;

import cn.chien.annotation.DataScope;
import cn.chien.core.auth.AuthThreadLocal;
import cn.chien.core.text.Convert;
import cn.chien.domain.BaseEntity;
import cn.chien.domain.entity.SysRole;
import cn.chien.domain.entity.SysUser;
import cn.chien.request.BaseRequest;
import cn.chien.security.auth.PermissionContextHolder;
import cn.chien.utils.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author qiandq3
 * @date 2022/12/2
 */
@Aspect
@Component
public class DataScopeAspect {
    
    /**
     * 全部数据权限
     */
    private static final String DATA_SCOPE_ALL = "1";
    
    /**
     * 自定数据权限
     */
    private static final String DATA_SCOPE_CUSTOM = "2";
    
    /**
     * 部门数据权限
     */
    private static final String DATA_SCOPE_DEPT = "3";
    
    /**
     * 部门及以下数据权限
     */
    private static final String DATA_SCOPE_DEPT_AND_CHILD = "4";
    
    /**
     * 仅本人数据权限
     */
    private static final String DATA_SCOPE_SELF = "5";
    
    /**
     * 数据权限过滤关键字
     */
    private static final String DATA_SCOPE = "dataScope";
    
    @Before("@annotation(dataScope)")
    public void doBefore(JoinPoint joinPoint, DataScope dataScope) {
        clearDataScope(joinPoint);
        handleDataScope(joinPoint, dataScope);
    }
    
    protected void handleDataScope(final JoinPoint joinPoint, DataScope dataScope) {
        // 获取当前的用户
        SysUser currentUser = AuthThreadLocal.getUser();
        if (currentUser != null) {
            // 如果是超级管理员，则不过滤数据
            if (!currentUser.isAdmin()) {
                String permission = StringUtils.defaultIfEmpty(dataScope.permission(),
                        PermissionContextHolder.getContext());
                dataScopeFilter(joinPoint, currentUser, dataScope.deptAlias(), dataScope.userAlias(), permission);
            }
        }
    }
    
    /**
     * 数据范围过滤
     *
     * @param joinPoint  切点
     * @param user       用户
     * @param deptAlias  部门别名
     * @param userAlias  用户别名
     * @param permission 权限字符
     */
    public static void dataScopeFilter(JoinPoint joinPoint, SysUser user, String deptAlias, String userAlias,
            String permission) {
        StringBuilder sqlString = new StringBuilder();
        List<String> conditions = new ArrayList<>();
        
        for (SysRole role : user.getRoles()) {
            String dataScope = role.getDataScope();
            if (!DATA_SCOPE_CUSTOM.equals(dataScope) && conditions.contains(dataScope)) {
                continue;
            }
            if (StringUtils.isNotEmpty(permission) && StringUtils.isNotEmpty(role.getPermissions())
                    && !CollectionUtils.containsAny(role.getPermissions(), Arrays.asList(Convert.toStrArray(permission)))) {
                continue;
            }
            if (DATA_SCOPE_ALL.equals(dataScope)) {
                sqlString = new StringBuilder();
                break;
            } else if (DATA_SCOPE_CUSTOM.equals(dataScope)) {
                sqlString.append(StringUtils.format(
                        " OR {}.dept_id IN ( SELECT dept_id FROM sys_role_dept WHERE role_id = {} ) ", deptAlias,
                        role.getRoleId()));
            } else if (DATA_SCOPE_DEPT.equals(dataScope)) {
                sqlString.append(StringUtils.format(" OR {}.dept_id = {} ", deptAlias, user.getDeptId()));
            } else if (DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope)) {
                sqlString.append(StringUtils.format(
                        " OR {}.dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} or find_in_set( {} , ancestors ) )",
                        deptAlias, user.getDeptId(), user.getDeptId()));
            } else if (DATA_SCOPE_SELF.equals(dataScope)) {
                if (StringUtils.isNotBlank(userAlias)) {
                    sqlString.append(StringUtils.format(" OR {}.user_id = {} ", userAlias, user.getUserId()));
                } else {
                    // 数据权限为仅本人且没有userAlias别名不查询任何数据
                    sqlString.append(StringUtils.format(" OR {}.dept_id = 0 ", deptAlias));
                }
            }
            conditions.add(dataScope);
        }
        
        if (StringUtils.isNotBlank(sqlString.toString())) {
            Object params = joinPoint.getArgs()[0];
            if (StringUtils.isNotNull(params)) {
                if (params instanceof BaseEntity baseEntity) {
                    baseEntity.getParams().put(DATA_SCOPE, " AND (" + sqlString.substring(4) + ")");
                } else if (params instanceof BaseRequest baseRequest) {
                    baseRequest.getParams().put(DATA_SCOPE, " AND (" + sqlString.substring(4) + ")");
                }
            }
        }
    }
    
    /**
     * 拼接权限sql前先清空params.dataScope参数防止注入
     */
    private void clearDataScope(final JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof BaseEntity baseEntity) {
                baseEntity.getParams().put(DATA_SCOPE, "");
            } else if (arg instanceof BaseRequest baseRequest) {
                baseRequest.getParams().put(DATA_SCOPE, null);
            }
        }
    }
    
}
