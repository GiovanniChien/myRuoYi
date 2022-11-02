package cn.chien.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author qian.diqi
 * @date 2022/10/19
 */
@Data
@ConfigurationProperties(prefix = "ruoyi.threadpool")
public class ThreadPoolProperties {
    
    private int corePoolSize;
    
    private int maxPoolSize;
    
    private int queueCapacity;
    
    private int keepAliveSeconds;
    
}
