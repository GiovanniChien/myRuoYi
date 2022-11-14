package cn.chien.web.service;

import cn.chien.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author qiandq3
 * @date 2022/11/10
 */
@Service("config")
public class ConfigService {
    
    @Autowired
    private ISysConfigService configService;
    
    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    public String getKey(String configKey) {
        return configService.selectConfigByKey(configKey);
    }
    
}
