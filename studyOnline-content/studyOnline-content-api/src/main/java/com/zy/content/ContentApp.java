package com.zy.content;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class ContentApp {
    public static void main(String[] args) {
        SpringApplication.run(ContentApp.class,args);
    }
}
