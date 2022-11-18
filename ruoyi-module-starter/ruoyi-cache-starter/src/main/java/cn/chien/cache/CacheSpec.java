package cn.chien.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qian.diqi
 * @date 2022/7/18
 */
public class CacheSpec {

    private String namespace;

    private Long maxInterval;

    private boolean refreshTtlAfterAccess;
    
    public String getNamespace() {
        return namespace;
    }
    
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    
    public Long getMaxInterval() {
        return maxInterval;
    }
    
    public void setMaxInterval(Long maxInterval) {
        this.maxInterval = maxInterval;
    }
    
    public boolean isRefreshTtlAfterAccess() {
        return refreshTtlAfterAccess;
    }
    
    public void setRefreshTtlAfterAccess(boolean refreshTtlAfterAccess) {
        this.refreshTtlAfterAccess = refreshTtlAfterAccess;
    }
}
