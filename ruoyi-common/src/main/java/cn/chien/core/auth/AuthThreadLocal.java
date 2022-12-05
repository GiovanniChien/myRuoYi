package cn.chien.core.auth;

import cn.chien.domain.entity.SysUser;
import org.springframework.core.NamedInheritableThreadLocal;

import java.util.HashMap;

/**
 * @author qiandq3
 * @date 2022/11/8
 */
public final class AuthThreadLocal {
    
    private static final ThreadLocal<HashMap<String, Object>> threadLocal = NamedInheritableThreadLocal.withInitial(
            HashMap::new);
    
    private AuthThreadLocal() {
    }
    
    public static void set(String key, Object val) {
        threadLocal.get().put(key, val);
    }
    
    public static Object get(String key) {
        return threadLocal.get() == null ? null : threadLocal.get().get(key);
    }
    
    public static String getString(String key) {
        Object val = get(key);
        return val == null ? null : val.toString();
    }
    
    public static Integer getInteger(String key) {
        Object val = get(key);
        return val == null ? null : Integer.valueOf(val.toString());
    }
    
    public static Long getLong(String key) {
        Object val = get(key);
        return val == null ? null : Long.parseLong(val.toString());
    }
    
    public static Long getUserId() {
        return getLong(AuthField.USER_ID.fieldName);
    }
    
    public static SysUser getUser() {
        return (SysUser) get("sysUser");
    }
    
    public static String getLoginName() {
        return getString(AuthField.LOGIN_NAME.fieldName);
    }
    
    public static void remove() {
        threadLocal.remove();
    }
    
    public enum AuthField {
        USER_ID("userId"),
        DEPT_ID("deptId"),
        LOGIN_NAME("loginName"),
        USER_NAME("userName"),
        USER_TYPE("userType"),
        EMAIL("email"),
        PHONENUMBER("phonenumber"),
        SEX("sex"),
        AVATAR("avatar"),
        PASSWORD("password"),
        DEPT("dept"),
        ROLES("roles");
        
        public final String fieldName;
        
        AuthField(String fieldName) {
            this.fieldName = fieldName;
        }
    }
    
}
