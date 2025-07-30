package com.sujiu.blog.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验注解
 *
 * @author sujiu
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * 必须有某个角色
     *
     * @return 角色名称
     */
    String mustRole() default "";

    /**
     * 是否必须登录
     *
     * @return true表示必须登录
     */
    boolean mustLogin() default true;
}

