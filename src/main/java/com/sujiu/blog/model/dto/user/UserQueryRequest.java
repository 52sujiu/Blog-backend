package com.sujiu.blog.model.dto.user;

import com.sujiu.blog.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理员用户查询请求
 *
 * @author sujiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "管理员用户查询请求")
public class UserQueryRequest extends PageRequest implements Serializable {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long id;

    /**
     * 搜索关键词（用户名、邮箱、昵称）
     */
    @Schema(description = "搜索关键词（用户名、邮箱、昵称）")
    private String keyword;

    /**
     * 用户状态：1-正常，0-禁用，-1-删除
     */
    @Schema(description = "用户状态：1-正常，0-禁用，-1-删除")
    private Integer status;

    /**
     * 用户角色：user-普通用户，admin-管理员，ban-封禁用户
     */
    @Schema(description = "用户角色：user-普通用户，admin-管理员，ban-封禁用户")
    private String role;

    /**
     * 注册开始时间
     */
    @Schema(description = "注册开始时间")
    private Date startTime;

    /**
     * 注册结束时间
     */
    @Schema(description = "注册结束时间")
    private Date endTime;

    private static final long serialVersionUID = 1L;
}