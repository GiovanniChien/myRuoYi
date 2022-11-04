package cn.chien.config;

import cn.chien.properties.SecurityProperties;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qian.diqi
 * @date 2022/7/2
 */
@Configuration
@ConditionalOnBean(SecurityProperties.class)
public class CaptchaConfig {


    @ConditionalOnProperty(prefix = "security.user", name = "captchaType", havingValue = "math", matchIfMissing = true)
    @Bean
    public CodeGenerator mathGenerator() {
        return new MathGenerator(1);
    }

    @ConditionalOnProperty(prefix = "security.user", name = "captchaType", havingValue = "code")
    @Bean
    public CodeGenerator codeGenerator() {
        return new RandomGenerator(4);
    }

    @Bean
    public ShearCaptcha captcha() {
        return CaptchaUtil.createShearCaptcha(160, 60);
    }

}
