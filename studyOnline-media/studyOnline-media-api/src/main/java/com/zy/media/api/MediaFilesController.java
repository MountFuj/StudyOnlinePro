package com.zy.media.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MediaFilesController {
    @GetMapping("/test")
    public String test(){
        return "test!";
    }
}
