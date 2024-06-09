package com.zy.content.feignclient.fallback;

import com.zy.content.feignclient.SearchClient;
import com.zy.content.model.po.CourseIndex;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SearchClientFallBackFactory implements FallbackFactory<SearchClient> {
    @Override
    public SearchClient create(Throwable throwable) {
        return new SearchClient() {
            @Override
            public Boolean add(CourseIndex courseIndex) {
                throwable.printStackTrace();
                log.debug("调用搜索发生熔断走降级方法,熔断异常:{}", throwable.getMessage());
                return false;
            }
        };
    }
}
