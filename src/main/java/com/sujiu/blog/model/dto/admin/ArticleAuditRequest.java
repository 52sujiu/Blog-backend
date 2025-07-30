package com.sujiu.blog.model.dto.admin;

import java.io.Serializable;
import lombok.Data;

/**
 * 文章审核请求
 *
 * @author sujiu
 */
@Data
public class ArticleAuditRequest implements Serializable {

    /**
     * 审核状态：2-通过，3-拒绝
     */
    private Integer status;

    /**
     * 审核原因
     */
    private String auditReason;

    private static final long serialVersionUID = 1L;
}
