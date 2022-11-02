package cn.chien.config;

import cn.chien.properties.ThreadPoolProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @author qian.diqi
 * @date 2022/10/19
 */
@Configuration
@EnableAsync
@EnableConfigurationProperties(ThreadPoolProperties.class)
public class ThreadExecutorConfig implements AsyncConfigurer {
    
    @Autowired
    private ThreadPoolProperties threadPoolProperties;
    
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程池大小
        executor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
        //最大线程数
        executor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
        //队列容量
        executor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
        //活跃时间
        executor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveSeconds());
        //线程名字前缀
        executor.setThreadNamePrefix("MyRuoYi-");
        executor.initialize();
        return executor;
    }
}
