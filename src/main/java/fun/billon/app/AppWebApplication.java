package fun.billon.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * springboot启动类
 *
 * @author billon
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = {
        "fun.billon.auth.api.feign",
        "fun.billon.member.api.feign",
        "fun.billon.forum.api.feign"})
@ComponentScan(basePackages = {
        "fun.billon.app",
        "fun.billon.auth.api.hystrix",
        "fun.billon.member.api.hystrix",
        "fun.billon.forum.api.hystrix"})
public class AppWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppWebApplication.class, args);
    }
}
