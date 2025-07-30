package com.sujiu.blog.model.vo.search;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 搜索高亮结果
 *
 * @author sujiu
 */
@Data
@Schema(description = "搜索高亮结果")
public class SearchHighlightVO implements Serializable {

    /**
     * 高亮标题
     */
    @Schema(description = "高亮标题")
    private String title;

    /**
     * 高亮内容片段
     */
    @Schema(description = "高亮内容片段")
    private String content;

    /**
     * 高亮摘要
     */
    @Schema(description = "高亮摘要")
    private String summary;

    private static final long serialVersionUID = 1L;
}
