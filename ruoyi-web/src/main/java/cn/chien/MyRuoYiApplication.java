package cn.chien;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * @author qian.diqi
 * @date 2022/7/2
 */
@SpringBootApplication(exclude = {RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class})
@MapperScan(basePackages = "cn.chien.**.mapper")
@EnableWebSecurity(debug = true)
public class MyRuoYiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyRuoYiApplication.class);
    }

}
