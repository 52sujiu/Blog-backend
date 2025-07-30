package com.sujiu.blog.model.dto.admin;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户状态更新请求
 *
 * @author sujiu
 */
@Data
public class UserStatusUpdateRequest implements Serializable {

    /**
     * 用户状态：1-正常，0-禁用，-1-删除
     */
    private Integer status;

    /**
     * 操作原因
     */
    private String reason;

    private static final long serialVersionUID = 1L;
}
