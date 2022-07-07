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
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private User user;

    private Cookie cookie;

    private Session session;

    private RememberMe rememberMe;

    @Data
    public static class User {
        private String loginUrl;

        private String indexUrl;

        private Boolean captchaEnabled;

        private String captchaType;

        private Password password;

        @Data
        public static class Password {
            private Integer maxRetryCount;

            private Integer lockTime;
        }
    }

    @Data
    public static class Cookie {
        private String domain;

        private String path;

        private String httpOnly;

        private Integer maxAge;
    }

    @Data
    public static class Session {
        private Integer expireTime;

        private Integer dbSyncPeriod;

        private Integer validationInterval;

        private Integer maxSession;

        private Boolean kickOutAfter;
    }

    public static class RememberMe {
        private Boolean enabled;
    }

}
