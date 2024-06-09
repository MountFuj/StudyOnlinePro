package com.zy.content;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.zy.content","com.zy.messagesdk"})
@EnableFeignClients(basePackages ={"com.zy.content.feignclient"})
public class ContentApp {
    public static void main(String[] args) {
        SpringApplication.run(ContentApp.class,args);
    }
}
