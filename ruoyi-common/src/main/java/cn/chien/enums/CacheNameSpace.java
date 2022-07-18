package cn.chien.enums;

/**
 * @author qian.diqi
 * @date 2022/7/18
 */
public enum CacheNameSpace {

    /**
     * 默认缓存
     */
    DEFAULT("default"),
    /**
     * 登录记录缓存
     */
    LOGIN_RECORD_CACHE("loginRecordCache");

    final String namespace;

    CacheNameSpace(String namespace) {
        this.namespace = namespace;
    }

    public String namespace() {
        return this.namespace;
    }

}
