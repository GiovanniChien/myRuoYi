package cn.chien.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author qian.diqi
 * @date 2022/7/18
 */
@ConfigurationProperties(prefix = "spring.cache")
@Data
public class CacheManagerProperties {

    private List<CacheSpec> manager;

}
