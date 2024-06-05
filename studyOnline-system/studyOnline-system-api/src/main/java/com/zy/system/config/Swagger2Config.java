package com.zy.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@EnableSwagger2WebMvc
@Configuration
public class Swagger2Config {


    private ApiInfo webApiInfo(){
        return new ApiInfoBuilder()
                .title("在线教育接口文档")
                .description("本文档描述了在线教育微服务接口定义")
                .version("1.0")
                .contact(new Contact("zhangYu","127.0.0.1","3385682934@qq.com"))
                .build();
    }

    @Bean
    public Docket webApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("系统接口")
                .apiInfo(webApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xuecheng.system.controller"))
                .paths(PathSelectors.regex("/system/.*"))
                .build();
    }
}
