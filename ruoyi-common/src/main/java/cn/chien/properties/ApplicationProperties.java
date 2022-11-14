package cn.chien.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author qiandq3
 * @date 2022/11/9
 */
@Data
@Component
@ConfigurationProperties(prefix = "ruoyi")
public class ApplicationProperties {
    
    /** 项目名称 */
    private String name;
    
    /** 版本 */
    private String version;
    
    /** 版权年份 */
    private String copyrightYear;
    
    /** 实例演示开关 */
    private boolean demoEnabled;
    
    /** 上传路径 */
    private String profile;
    
    /** 获取地址开关 */
    private boolean addressEnabled;
    
}
