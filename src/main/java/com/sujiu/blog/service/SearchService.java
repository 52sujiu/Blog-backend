package com.sujiu.blog.service;

import com.sujiu.blog.model.dto.search.SearchRequest;
import com.sujiu.blog.model.vo.search.SearchResultVO;

import java.util.List;

/**
 * 搜索服务
 *
 * @author sujiu
 */
public interface SearchService {

    /**
     * 全文搜索
     *
     * @param searchRequest 搜索请求
     * @return 搜索结果
     */
    SearchResultVO search(SearchRequest searchRequest);

    /**
     * 获取搜索建议
     *
     * @param keyword 关键词
     * @param limit 建议数量限制
     * @return 搜索建议列表
     */
    List<String> getSearchSuggestions(String keyword, Integer limit);
}
