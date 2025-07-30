package com.sujiu.blog.model.vo.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户注册响应
 *
 * @author sujiu
 */
@Data
public class UserRegisterVO implements Serializable {

    /**
     * 用户ID
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}
