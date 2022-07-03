package cn.chien;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author qian.diqi
 * @date 2022/7/2
 */
@SpringBootApplication
@MapperScan(basePackages = "cn.chien.**.mapper")
public class MyRuoYiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyRuoYiApplication.class);
    }

}
