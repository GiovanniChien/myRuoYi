package cn.chien.utils;

/**
 * @author qiandq3
 * @date 2022/12/6
 */
public class ReflectionUtils extends org.springframework.util.ReflectionUtils {
    
    public static boolean isPrimitive(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return true;
        }
        return Integer.class.isAssignableFrom(clazz)
                || Byte.class.isAssignableFrom(clazz)
                || Long.class.isAssignableFrom(clazz)
                || Double.class.isAssignableFrom(clazz)
                || Float.class.isAssignableFrom(clazz)
                || Character.class.isAssignableFrom(clazz)
                || Short.class.isAssignableFrom(clazz)
                || Boolean.class.isAssignableFrom(clazz)
                || String.class.isAssignableFrom(clazz);
    }
    
}
