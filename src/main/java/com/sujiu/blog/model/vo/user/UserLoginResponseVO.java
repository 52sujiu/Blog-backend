package com.sujiu.blog.model.vo.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户登录响应
 *
 * @author sujiu
 */
@Data
public class UserLoginResponseVO implements Serializable {

    /**
     * 用户信息
     */
    private LoginUserVO user;

    private static final long serialVersionUID = 1L;
}
