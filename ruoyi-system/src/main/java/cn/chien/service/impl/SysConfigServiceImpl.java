package cn.chien.service.impl;

import cn.chien.cache.CacheClient;
import cn.chien.constant.UserConstants;
import cn.chien.domain.SysConfig;
import cn.chien.enums.CacheNameSpace;
import cn.chien.exception.ServiceException;
import cn.chien.mapper.SysConfigMapper;
import cn.chien.service.ISysConfigService;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * 参数配置 服务层实现
 *
 * @author ruoyi
 */
@Service
@CacheConfig(cacheNames = "sysConfig")
public class SysConfigServiceImpl implements ISysConfigService {
    
    private static final String CACHE_PREFIX = "SYS:CONFIG:";
    
    @Resource
    private SysConfigMapper configMapper;
    
    /**
     * 项目启动时，初始化参数到缓存
     */
    @PostConstruct
    public void init() {
        resetConfigCache();
    }
    
    /**
     * 查询参数配置信息
     *
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    @Override
    @Cacheable(key = CACHE_PREFIX + "ID:" + "#configId")
    public SysConfig selectConfigById(Long configId) {
        return configMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigId, configId));
    }
    
    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数key
     * @return 参数键值
     */
    @Override
    @Cacheable(key = CACHE_PREFIX + "KEY:" + "#configKey")
    public String selectConfigByKey(String configKey) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey,
                configKey);
        SysConfig retConfig = configMapper.selectOne(wrapper);
        if (retConfig != null) {
            return retConfig.getConfigValue();
        }
        return StringUtils.EMPTY;
    }
    
    /**
     * 查询参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<SysConfig> selectConfigList(SysConfig config) {
        return configMapper.selectConfigList(config);
    }
    
    /**
     * 新增参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int insertConfig(SysConfig config) {
        return configMapper.insert(config);
    }
    
    /**
     * 修改参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    @Caching(evict = {
            @CacheEvict(key = CACHE_PREFIX + "ID:" + "#config.configId"),
            @CacheEvict(key = CACHE_PREFIX + "KEY:" + "#config.configKey")
    })
    public int updateConfig(SysConfig config) {
        return configMapper.updateConfig(config);
    }
    
    /**
     * 批量删除参数配置对象
     *
     * @param ids 需要删除的数据ID
     */
    @Override
    public void deleteConfigByIds(String ids) {
        Long[] configIds = Convert.toLongArray(ids);
        for (Long configId : configIds) {
            SysConfig config = selectConfigById(configId);
            if (StringUtils.equals(UserConstants.YES, config.getConfigType())) {
                throw new ServiceException(String.format("内置参数【%1$s】不能删除 ", config.getConfigKey()));
            }
            configMapper.deleteConfigById(configId);
        }
        for (Long configId : configIds) {
            CacheClient.evict(CACHE_PREFIX + "ID:" + configId);
        }
    }
    
    /**
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache() {
        List<SysConfig> configsList = configMapper.selectConfigList(new SysConfig());
        for (SysConfig config : configsList) {
            CacheClient.put(CacheNameSpace.SYS_CONFIG.namespace(), CACHE_PREFIX + "ID:" + config.getConfigId(), config);
            CacheClient.put(CacheNameSpace.SYS_CONFIG.namespace(), CACHE_PREFIX + "KEY:" + config.getConfigKey(), config);
        }
    }
    
    /**
     * 清空参数缓存数据
     */
    @Override
    public void clearConfigCache() {
        CacheClient.evictAllEntries(CacheNameSpace.SYS_CONFIG.namespace());
    }
    
    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache() {
        clearConfigCache();
        loadingConfigCache();
    }
    
    /**
     * 校验参数键名是否唯一
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public String checkConfigKeyUnique(SysConfig config) {
        long configId = config.getConfigId() == null ? -1L : config.getConfigId();
        SysConfig info = configMapper.checkConfigKeyUnique(config.getConfigKey());
        if (info != null && info.getConfigId() != configId) {
            return UserConstants.CONFIG_KEY_NOT_UNIQUE;
        }
        return UserConstants.CONFIG_KEY_UNIQUE;
    }
}
