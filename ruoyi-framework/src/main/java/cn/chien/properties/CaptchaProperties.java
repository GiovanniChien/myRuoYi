package cn.chien.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author qian.diqi
 * @date 2022/7/2
 */
@Data
@Component
@ConfigurationProperties(prefix = "ruoyi.captcha")
public class CaptchaProperties {

    private String type;

    private Integer codeLength = 4;

    private String codeBaseStr = "abcdefghijklmnopqrstuvwxyz0123456789";

}
