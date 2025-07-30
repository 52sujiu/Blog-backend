package com.sujiu.blog.controller;

import com.sujiu.blog.common.BaseResponse;
import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.common.ResultUtils;
import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.model.dto.search.SearchRequest;
import com.sujiu.blog.model.vo.search.SearchResultVO;
import com.sujiu.blog.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 搜索控制器
 *
 * @author sujiu
 */
@RestController
@RequestMapping("/search")
@Slf4j
@Tag(name = "搜索功能", description = "全文搜索相关接口")
public class SearchController {

    @Resource
    private SearchService searchService;

    /**
     * 全文搜索
     *
     * @param keyword 搜索关键词
     * @param type 搜索类型
     * @param current 当前页码
     * @param size 每页大小
     * @param sortField 排序字段
     * @param sortOrder 排序方式
     * @return 搜索结果
     */
    @Operation(summary = "全文搜索", description = "支持搜索文章、用户、标签、分类等内容")
    @GetMapping
    public BaseResponse<SearchResultVO> search(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "搜索类型：article-文章，user-用户，tag-标签，category-分类，all-全部") 
            @RequestParam(required = false, defaultValue = "all") String type,
            @Parameter(description = "当前页码") @RequestParam(required = false, defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(required = false, defaultValue = "10") Integer size,
            @Parameter(description = "排序字段：relevance-相关性，time-时间，views-浏览量") 
            @RequestParam(required = false, defaultValue = "relevance") String sortField,
            @Parameter(description = "排序方式：asc-升序，desc-降序") 
            @RequestParam(required = false, defaultValue = "desc") String sortOrder) {

        log.info("收到搜索请求，关键词：{}，类型：{}，页码：{}，大小：{}", keyword, type, current, size);

        if (StringUtils.isBlank(keyword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "搜索关键词不能为空");
        }

        // 构建搜索请求
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setKeyword(keyword.trim());
        searchRequest.setType(type);
        searchRequest.setCurrent(current);
        searchRequest.setSize(size);
        searchRequest.setSortField(sortField);
        searchRequest.setSortOrder(sortOrder);

        SearchResultVO result = searchService.search(searchRequest);
        return ResultUtils.success(result, "搜索成功");
    }

    /**
     * 使用POST方式进行搜索（支持复杂搜索条件）
     *
     * @param searchRequest 搜索请求
     * @return 搜索结果
     */
    @Operation(summary = "高级搜索", description = "支持复杂搜索条件的POST方式搜索")
    @PostMapping
    public BaseResponse<SearchResultVO> advancedSearch(@RequestBody SearchRequest searchRequest) {
        log.info("收到高级搜索请求：{}", searchRequest);

        if (searchRequest == null || StringUtils.isBlank(searchRequest.getKeyword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "搜索关键词不能为空");
        }

        SearchResultVO result = searchService.search(searchRequest);
        return ResultUtils.success(result, "搜索成功");
    }

    /**
     * 获取搜索建议
     *
     * @param keyword 关键词
     * @param limit 建议数量限制
     * @return 搜索建议列表
     */
    @Operation(summary = "获取搜索建议", description = "根据输入的关键词获取搜索建议")
    @GetMapping("/suggest")
    public BaseResponse<List<String>> getSearchSuggestions(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "建议数量限制") @RequestParam(required = false, defaultValue = "10") Integer limit) {

        log.info("获取搜索建议，关键词：{}，限制：{}", keyword, limit);

        if (StringUtils.isBlank(keyword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "搜索关键词不能为空");
        }

        List<String> suggestions = searchService.getSearchSuggestions(keyword.trim(), limit);
        return ResultUtils.success(suggestions, "获取搜索建议成功");
    }
}
