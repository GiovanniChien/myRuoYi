package cn.chien.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author qian.diqi
 * @date 2022/7/18
 */
@ConfigurationProperties(prefix = "spring.cache")
public class CacheManagerProperties {

    private List<CacheSpec> manager;
    
    public List<CacheSpec> getManager() {
        return manager;
    }
    
    public void setManager(List<CacheSpec> manager) {
        this.manager = manager;
    }
}
