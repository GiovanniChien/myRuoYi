package cn.chien.config;

import cn.chien.properties.CaptchaProperties;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qian.diqi
 * @date 2022/7/2
 */
@Configuration
@ConditionalOnBean(CaptchaProperties.class)
public class CaptchaConfig {

    @Autowired
    private CaptchaProperties captchaProperties;

    @ConditionalOnProperty(prefix = "ruoyi.user", name = "captchaType", havingValue = "math", matchIfMissing = true)
    @Bean
    public CodeGenerator mathGenerator() {
        return new MathGenerator(1);
    }

    @ConditionalOnProperty(prefix = "ruoyi.user", name = "captchaType", havingValue = "code")
    @Bean
    public CodeGenerator codeGenerator() {
        return new RandomGenerator(captchaProperties.getCodeBaseStr(), captchaProperties.getCodeLength());
    }

    @Bean
    public ShearCaptcha captcha() {
        return CaptchaUtil.createShearCaptcha(160, 60);
    }

}
