package com.sujiu.blog.aop;

import com.sujiu.blog.annotation.AuthCheck;
import com.sujiu.blog.annotation.RequireAdmin;
import com.sujiu.blog.annotation.RequireLogin;
import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.constant.UserConstant;
import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.model.entity.User;
import com.sujiu.blog.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
/**
 * 权限校验 AOP
 *
 * @author sujiu
 */
@Aspect
@Component
@Slf4j
public class AuthInterceptor {
    @Resource
    private UserService userService;

    /**
     * AuthCheck注解拦截器
     *
     * @param joinPoint
     * @param authCheck
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object doAuthCheck(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        boolean mustLogin = authCheck.mustLogin();

        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 如果需要登录，检查登录状态
        if (mustLogin) {
            User loginUser = userService.getLoginUserPermitNull(request);
            if (loginUser == null) {
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
            }

            // 检查用户是否被禁用
            if (UserConstant.BAN_ROLE.equals(loginUser.getRole())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "账号已被禁用");
            }

            // 如果需要特定角色
            if (StringUtils.isNotBlank(mustRole)) {
                if (!mustRole.equals(loginUser.getRole())) {
                    throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "权限不足");
                }
            }
        }

        // 通过权限校验，放行
        return joinPoint.proceed();
    }

    /**
     * RequireLogin注解拦截器
     *
     * @param joinPoint
     * @return
     */
    @Around("@annotation(com.sujiu.blog.annotation.RequireLogin)")
    public Object doRequireLogin(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        }

        // 检查用户是否被禁用
        if (UserConstant.BAN_ROLE.equals(loginUser.getRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "账号已被禁用");
        }

        return joinPoint.proceed();
    }

    /**
     * RequireAdmin注解拦截器
     *
     * @param joinPoint
     * @return
     */
    @Around("@annotation(com.sujiu.blog.annotation.RequireAdmin)")
    public Object doRequireAdmin(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        }

        // 检查用户是否被禁用
        if (UserConstant.BAN_ROLE.equals(loginUser.getRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "账号已被禁用");
        }

        // 检查是否为管理员
        if (!UserConstant.ADMIN_ROLE.equals(loginUser.getRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "需要管理员权限");
        }

        return joinPoint.proceed();
    }
}
