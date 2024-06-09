package com.zy.content.feignclient;

import com.zy.content.feignclient.fallback.SearchClientFallBackFactory;
import com.zy.content.model.po.CourseIndex;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "search",fallbackFactory = SearchClientFallBackFactory.class)
public interface SearchClient {
    @PostMapping("/search/index/course")
    public Boolean add(@RequestBody CourseIndex courseIndex);
}
