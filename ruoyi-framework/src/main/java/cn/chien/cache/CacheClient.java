package cn.chien.cache;

import cn.chien.enums.CacheNameSpace;
import cn.chien.utils.spring.SpringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * @author qian.diqi
 * @date 2022/7/18
 */
public class CacheClient {

    private static final CacheManager cacheManager = SpringUtils.getBean(CacheManager.class);

    public static Cache getCache(String name) {
        return cacheManager.getCache(name);
    }

    public static Cache getCache() {
        return getCache(CacheNameSpace.DEFAULT.namespace());
    }

    public static <T> T get(String cacheNameSpace, Object key, Class<T> clazz) {
        Cache cache = cacheManager.getCache(cacheNameSpace);
        if (cache != null) {
            return cache.get(key, clazz);
        }
        return null;
    }

    public static <T> T get(Object key, Class<T> clazz) {
        return get(CacheNameSpace.DEFAULT.namespace(), key, clazz);
    }

    public static void put(String cacheNameSpace, Object key, Object value) {
        Cache cache = getCache(cacheNameSpace);
        if (cache != null) {
            cache.put(key, value);
        }
    }

    public static void put(Object key, Object value) {
        put(CacheNameSpace.DEFAULT.namespace(), key, value);
    }

    public static Cache.ValueWrapper putIfAbsent(String cacheNameSpace, Object key, Object value) {
        Cache cache = getCache(cacheNameSpace);
        if (cache != null) {
            return cache.putIfAbsent(key, value);
        }
        return null;
    }

    public static Cache.ValueWrapper putIfAbsent(Object key, Object value) {
        return putIfAbsent(CacheNameSpace.DEFAULT.namespace(), key, value);
    }

    public static boolean evictIfPresent(Object key) {
        return evictIfPresent(CacheNameSpace.DEFAULT.namespace(), key);
    }

    public static boolean evictIfPresent(String cacheNameSpace, Object key) {
        Cache cache = getCache(cacheNameSpace);
        if (cache != null) {
            return cache.evictIfPresent(key);
        }
        return false;
    }

    public static void evict(String cacheNameSpace, Object key) {
        Cache cache = getCache(cacheNameSpace);
        if (cache != null) {
            cache.evict(key);
        }
    }

    public static void evict(Object key) {
        evict(CacheNameSpace.DEFAULT.namespace(), key);
    }

}
