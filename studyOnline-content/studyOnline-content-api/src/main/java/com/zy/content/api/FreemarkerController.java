package com.zy.content.api;

import com.zy.content.config.MultipartSupportConfig;
import com.zy.content.feignclient.MediaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;

@Controller
public class FreemarkerController {
    @Autowired
    MediaClient  mediaClient;
    @GetMapping("/testfreemarker")
    public ModelAndView test(){
        ModelAndView modelAndView = new ModelAndView();
        //设置模型数据
        modelAndView.addObject("name","小明");
        //设置模板名称
        modelAndView.setViewName("test");
        return modelAndView;
    }

    @GetMapping("/testupload")
    @ResponseBody
    public String testupload(){
        System.out.println("=====测试执行====");
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(new File("D:\\Nacos.html"));
        mediaClient.upload(multipartFile,"/course/test.html");
        return "success";
    }
}
