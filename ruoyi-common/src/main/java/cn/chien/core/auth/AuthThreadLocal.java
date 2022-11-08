package cn.chien.core.auth;

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
        return threadLocal.get().get(key);
    }
    
    public static String getString(String key) {
        return (String) get(key);
    }
    
    public static Integer getInteger(String key) {
        return (Integer) get(key);
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
        
        public String fieldName;
        
        AuthField(String fieldName) {
            this.fieldName = fieldName;
        }
    }
    
}
