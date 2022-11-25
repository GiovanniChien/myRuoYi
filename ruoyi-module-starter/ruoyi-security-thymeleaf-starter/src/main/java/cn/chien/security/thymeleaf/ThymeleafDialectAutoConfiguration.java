package cn.chien.security.thymeleaf;

import cn.chien.security.thymeleaf.dialect.RuoyiDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiandq3
 * @date 2022/11/21
 */
@Configuration(proxyBeanMethods = false)
public class ThymeleafDialectAutoConfiguration {

    @Bean
    public RuoyiDialect ruoyiDialect() {
        return new RuoyiDialect();
    }
    
}
