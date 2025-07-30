package com.sujiu.blog.model.dto.tag;

import com.sujiu.blog.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签查询请求
 *
 * @author sujiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "标签查询请求")
public class TagQueryRequest extends PageRequest implements Serializable {

    /**
     * 搜索关键词
     */
    @Schema(description = "搜索关键词")
    private String keyword;

    /**
     * 状态：1-正常，0-禁用
     */
    @Schema(description = "状态：1-正常，0-禁用")
    private Integer status;

    /**
     * 排序字段：name-名称，articleCount-文章数量，createTime-创建时间
     */
    @Schema(description = "排序字段：name-名称，articleCount-文章数量，createTime-创建时间")
    private String sortField;

    /**
     * 排序方式：asc-升序，desc-降序
     */
    @Schema(description = "排序方式：asc-升序，desc-降序")
    private String sortOrder;

    private static final long serialVersionUID = 1L;
}
