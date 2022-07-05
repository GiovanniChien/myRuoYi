package cn.chien;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

/**
 * @author qian.diqi
 * @date 2022/7/2
 */
@SpringBootApplication
@MapperScan(basePackages = "cn.chien.**.mapper")
@EnableWebSecurity(debug = true)
@EnableSpringHttpSession
public class MyRuoYiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyRuoYiApplication.class);
    }

}
