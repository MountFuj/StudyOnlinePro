package com.xuecheng.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.search.dto.SearchCourseParamDto;
import com.xuecheng.search.dto.SearchPageResultDto;
import com.xuecheng.search.po.CourseIndex;
import com.xuecheng.search.service.CourseSearchService;
import com.zy.base.model.PageParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CourseSearchServiceImpl implements CourseSearchService {

    @Value("${elasticsearch.course.index}")
    private String indexName;

    @Value("${elasticsearch.course.source_fields}")
    private String sourceFiled;

    @Autowired
    RestHighLevelClient client;

    @Override
    public SearchPageResultDto<CourseIndex> queryCoursePubIndex(PageParams pageParams, SearchCourseParamDto searchCourseParamDto) {
        // 设置索引
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // source源字段过滤
        String[] split = sourceFiled.split(",");
        sourceBuilder.fetchSource(split,new String[]{});

        if(searchCourseParamDto==null){
            searchCourseParamDto = new SearchCourseParamDto();
        }
        // 关键字
        if(StringUtils.isNotEmpty(searchCourseParamDto.getKeywords())){
            // 匹配关键字
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(searchCourseParamDto.getKeywords(), "name", "description");
            // 设置匹配占比
            multiMatchQueryBuilder.minimumShouldMatch("70%");
            // 提升另一个字段的boost值
            multiMatchQueryBuilder.field("name",10);
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
        // 过滤
        if(StringUtils.isNotEmpty(searchCourseParamDto.getSt())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("stName",searchCourseParamDto.getSt()));
        }
        if(StringUtils.isNotEmpty(searchCourseParamDto.getMt())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("mtName",searchCourseParamDto.getMt()));
        }
        if(StringUtils.isNotEmpty(searchCourseParamDto.getGrade())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade",searchCourseParamDto.getGrade()));
        }
        // 分页
        long pageNo = pageParams.getPageNo();
        long pageSize = pageParams.getPageSize();
        int from = (int) ((pageNo - 1) * pageSize);
        sourceBuilder.from(from);
        sourceBuilder.size(Math.toIntExact(pageSize));
        // 布尔查询
        searchRequest.source().query(boolQueryBuilder);
        // 高亮设置
        sourceBuilder.highlighter(SearchSourceBuilder.highlight().field("name")
                .preTags("<font class = 'eslight'>")
                .postTags("</font>"));
        // 搜索
        searchRequest.source(sourceBuilder);
        // 聚合设置
        buildAggregation(searchRequest);
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        }catch (Exception e){
            log.error("搜索课程出错:{}",e.getMessage());
            return new SearchPageResultDto<>(new ArrayList<>(),0,pageNo,pageSize);
        }

        // 结果集处理
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        // 记录总数
        long count = hits.getTotalHits().value;
        // 数据列表
        List<CourseIndex> list = new ArrayList<>();
        for(SearchHit hit : searchHits){
            String json = hit.getSourceAsString();
            CourseIndex courseIndex = JSON.parseObject(json, CourseIndex.class);
            // 取出source
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            // 取出名称
            String name = courseIndex.getName();
            // 取出高亮字段内容
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if(highlightFields!=null){
                HighlightField field = highlightFields.get("name");
                if(field!=null){
                    Text[] fragments = field.getFragments();
                    StringBuffer stringBuffer = new StringBuffer();
                    for (Text text : fragments){
                        stringBuffer.append(text.string());
                    }
                    name = stringBuffer.toString();
                }
            }
            courseIndex.setName(name);
            list.add(courseIndex);
        }
        // 获取聚合结果
        List<String> mtList = getAggregation(searchResponse.getAggregations(),"mtAgg");
        List<String> stList = getAggregation(searchResponse.getAggregations(), "stAgg");
        SearchPageResultDto<CourseIndex> result = new SearchPageResultDto<>(list, count, pageNo, pageSize);
        result.setMtList(mtList);
        result.setStList(stList);
        return result;
    }

    private List<String> getAggregation(Aggregations aggregations, String aggName) {
        Terms terms = aggregations.get(aggName);
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        List<String> list = new ArrayList<>();
        for (Terms.Bucket bucket : buckets){
            String key = bucket.getKeyAsString();
            list.add(key);
        }
        return list;
    }

    private void buildAggregation(SearchRequest request){
        request.source().aggregation(
                AggregationBuilders.terms("mtAgg")
                        .field("mtName")
                        .size(100)
        );
        request.source().aggregation(
                AggregationBuilders.terms("stAgg")
                        .field("stName")
                        .size(100)
        );
    }
}
