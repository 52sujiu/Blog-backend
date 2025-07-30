package com.sujiu.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sujiu.blog.model.dto.search.SearchRequest;
import com.sujiu.blog.model.vo.search.SearchResultVO;
import com.sujiu.blog.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * 搜索控制器测试
 *
 * @author sujiu
 */
@SpringBootTest
@AutoConfigureMockMvc
public class SearchControllerTest {

    @Resource
    private MockMvc mockMvc;

    @MockBean
    private SearchService searchService;

    @Resource
    private ObjectMapper objectMapper;

    @Test
    void testSearchSuccess() throws Exception {
        // Mock搜索结果
        SearchResultVO mockResult = new SearchResultVO();
        SearchResultVO.ArticleSearchResult articleResult = new SearchResultVO.ArticleSearchResult();
        articleResult.setTotal(1L);
        mockResult.setArticles(articleResult);

        when(searchService.search(any(SearchRequest.class))).thenReturn(mockResult);

        mockMvc.perform(MockMvcRequestBuilders.get("/search")
                        .param("keyword", "Spring Boot")
                        .param("type", "article")
                        .param("current", "1")
                        .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("搜索成功"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.articles.total").value(1));
    }

    @Test
    void testSearchWithEmptyKeyword() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/search")
                        .param("keyword", "")
                        .param("type", "article"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(40000));
    }

    @Test
    void testAdvancedSearch() throws Exception {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setKeyword("Spring Boot");
        searchRequest.setType("article");
        searchRequest.setCurrent(1);
        searchRequest.setSize(10);

        SearchResultVO mockResult = new SearchResultVO();
        when(searchService.search(any(SearchRequest.class))).thenReturn(mockResult);

        mockMvc.perform(MockMvcRequestBuilders.post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("搜索成功"));
    }

    @Test
    void testGetSearchSuggestions() throws Exception {
        List<String> mockSuggestions = Arrays.asList("Spring Boot", "Spring Cloud", "Spring Security");
        when(searchService.getSearchSuggestions(anyString(), anyInt())).thenReturn(mockSuggestions);

        mockMvc.perform(MockMvcRequestBuilders.get("/search/suggest")
                        .param("keyword", "Spring")
                        .param("limit", "5"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("获取搜索建议成功"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(3));
    }

    @Test
    void testGetSearchSuggestionsWithEmptyKeyword() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/search/suggest")
                        .param("keyword", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(40000));
    }
}
