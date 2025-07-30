package com.sujiu.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.constant.UserConstant;
import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.mapper.UserMapper;
import com.sujiu.blog.model.dto.user.PasswordUpdateRequest;
import com.sujiu.blog.model.dto.user.UserLoginRequest;
import com.sujiu.blog.model.dto.user.UserQueryRequest;
import com.sujiu.blog.model.dto.user.UserRegisterRequest;
import com.sujiu.blog.model.dto.user.UserRoleUpdateRequest;
import com.sujiu.blog.model.dto.user.UserStatusUpdateRequest;
import com.sujiu.blog.model.dto.user.UserUpdateRequest;
import com.sujiu.blog.model.entity.User;
import com.sujiu.blog.model.vo.user.AdminUserVO;
import com.sujiu.blog.model.vo.user.LoginUserVO;
import com.sujiu.blog.model.vo.user.UserVO;
import com.sujiu.blog.model.vo.common.PageVO;
import com.sujiu.blog.service.FollowService;
import com.sujiu.blog.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 *
 * @author sujiu
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "sujiu";

    /**
     * 密码加密器
     */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户名正则：3-20字符，字母数字下划线
     */
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    /**
     * 邮箱正则
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    /**
     * 密码正则：6-20字符
     */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{6,20}$");

    @Resource
    private FollowService followService;

    @Override
    public Long userRegister(UserRegisterRequest userRegisterRequest, HttpServletRequest request) {
        // 1. 参数校验
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        String username = userRegisterRequest.getUsername();
        String email = userRegisterRequest.getEmail();
        String password = userRegisterRequest.getPassword();
        String confirmPassword = userRegisterRequest.getConfirmPassword();

        // 检查必填字段
        if (StringUtils.isAnyBlank(username, email, password, confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名、邮箱、密码不能为空");
        }

        // 2. 格式校验
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名格式不正确，应为3-20字符的字母数字下划线");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式不正确");
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码格式不正确，应为6-20字符");
        }

        // 3. 密码确认校验
        if (!password.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        // 4. 检查用户名是否已存在
        QueryWrapper<User> usernameQueryWrapper = new QueryWrapper<>();
        usernameQueryWrapper.eq("username", username);
        long usernameCount = this.baseMapper.selectCount(usernameQueryWrapper);
        if (usernameCount > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名已存在");
        }

        // 5. 检查邮箱是否已存在
        QueryWrapper<User> emailQueryWrapper = new QueryWrapper<>();
        emailQueryWrapper.eq("email", email);
        long emailCount = this.baseMapper.selectCount(emailQueryWrapper);
        if (emailCount > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱已被注册");
        }

        // 6. 密码加密
        String encryptPassword = passwordEncoder.encode(password);

        // 7. 创建用户对象
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encryptPassword);
        user.setRole("user"); // 默认角色为普通用户
        user.setStatus(1); // 默认状态为正常
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        // 8. 保存用户
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库异常");
        }

        log.info("用户注册成功，用户ID：{}，用户名：{}，邮箱：{}", user.getId(), username, email);
        return user.getId();
    }

    @Override
    public LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 1. 参数校验
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        String account = userLoginRequest.getAccount();
        String password = userLoginRequest.getPassword();
        Boolean rememberMe = userLoginRequest.getRememberMe();

        // 检查必填字段
        if (StringUtils.isAnyBlank(account, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号和密码不能为空");
        }

        // 2. 格式校验
        if (account.length() < 3 || account.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度应为3-50字符");
        }

        if (password.length() < 6 || password.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度应为6-20字符");
        }

        // 3. 查询用户（支持用户名或邮箱登录）
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq("username", account).or().eq("email", account));
        User user = this.baseMapper.selectOne(queryWrapper);

        // 4. 验证用户是否存在
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }

        // 5. 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }

        // 6. 检查用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "账号已被禁用");
        }

        // 7. 更新登录信息
        user.setLastLoginTime(new Date());
        user.setLastLoginIp(getClientIpAddress(request));
        this.updateById(user);

        // 8. 记录登录状态到session
        HttpSession session = request.getSession();
        session.setAttribute(UserConstant.USER_LOGIN_STATE, user);

        // 9. 设置session过期时间（如果选择记住登录）
        if (rememberMe != null && rememberMe) {
            // 记住登录：30天
            session.setMaxInactiveInterval(30 * 24 * 60 * 60);
        } else {
            // 不记住登录：2小时
            session.setMaxInactiveInterval(2 * 60 * 60);
        }

        // 10. 返回脱敏的用户信息
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);

        log.info("用户登录成功，用户ID：{}，用户名：{}，IP：{}", user.getId(), user.getUsername(), getClientIpAddress(request));
        return loginUserVO;
    }

    @Override
    public Boolean userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 获取当前登录用户
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        }

        // 移除登录状态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);

        // 使session失效
        request.getSession().invalidate();

        log.info("用户注销成功");
        return true;
    }

    @Override
    public LoginUserVO getCurrentUser(HttpServletRequest request) {
        // 获取当前登录用户（已验证权限）
        User currentUser = getCurrentLoginUser(request);

        // 返回脱敏的用户信息
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(currentUser, loginUserVO);

        return loginUserVO;
    }

    @Override
    public Boolean updateProfile(UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        // 1. 参数校验
        if (userUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 2. 获取当前登录用户（已验证权限）
        User user = getCurrentLoginUser(request);
        Long userId = user.getId();

        // 3. 参数格式校验
        String nickname = userUpdateRequest.getNickname();
        String avatar = userUpdateRequest.getAvatar();
        String phone = userUpdateRequest.getPhone();
        Integer gender = userUpdateRequest.getGender();
        String birthday = userUpdateRequest.getBirthday();
        String bio = userUpdateRequest.getBio();
        String website = userUpdateRequest.getWebsite();
        String location = userUpdateRequest.getLocation();

        // 昵称校验
        if (StringUtils.isNotBlank(nickname)) {
            if (nickname.length() < 1 || nickname.length() > 50) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "昵称长度应为1-50字符");
            }
        }

        // 手机号校验
        if (StringUtils.isNotBlank(phone)) {
            if (!phone.matches("^1[3-9]\\d{9}$")) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号格式不正确");
            }
        }

        // 性别校验
        if (gender != null && (gender < 0 || gender > 2)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "性别参数不正确");
        }

        // 生日校验
        Date birthdayDate = null;
        if (StringUtils.isNotBlank(birthday)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                birthdayDate = sdf.parse(birthday);
                // 检查生日不能是未来时间
                if (birthdayDate.after(new Date())) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "生日不能是未来时间");
                }
            } catch (ParseException e) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "生日格式不正确，应为yyyy-MM-dd");
            }
        }

        // 个人简介校验
        if (StringUtils.isNotBlank(bio) && bio.length() > 500) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "个人简介不能超过500字符");
        }

        // 网站URL校验
        if (StringUtils.isNotBlank(website)) {
            if (!website.matches("^https?://.*")) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "网站地址格式不正确");
            }
        }

        // 6. 更新用户信息
        User updateUser = new User();
        updateUser.setId(userId);

        if (StringUtils.isNotBlank(nickname)) {
            updateUser.setNickname(nickname);
        }
        if (StringUtils.isNotBlank(avatar)) {
            updateUser.setAvatar(avatar);
        }
        if (StringUtils.isNotBlank(phone)) {
            updateUser.setPhone(phone);
        }
        if (gender != null) {
            updateUser.setGender(gender);
        }
        if (birthdayDate != null) {
            updateUser.setBirthday(birthdayDate);
        }
        if (StringUtils.isNotBlank(bio)) {
            updateUser.setBio(bio);
        }
        if (StringUtils.isNotBlank(website)) {
            updateUser.setWebsite(website);
        }
        if (StringUtils.isNotBlank(location)) {
            updateUser.setLocation(location);
        }

        updateUser.setUpdatedTime(new Date());

        // 7. 执行更新
        boolean updateResult = this.updateById(updateUser);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败，数据库异常");
        }

        log.info("用户更新个人信息成功，用户ID：{}", userId);
        return true;
    }

    @Override
    public Boolean updatePassword(PasswordUpdateRequest passwordUpdateRequest, HttpServletRequest request) {
        // 1. 参数校验
        if (passwordUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        String oldPassword = passwordUpdateRequest.getOldPassword();
        String newPassword = passwordUpdateRequest.getNewPassword();
        String confirmPassword = passwordUpdateRequest.getConfirmPassword();

        // 检查必填字段
        if (StringUtils.isAnyBlank(oldPassword, newPassword, confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "原密码、新密码、确认密码不能为空");
        }

        // 2. 获取当前登录用户（已验证权限）
        User user = getCurrentLoginUser(request);
        Long userId = user.getId();

        // 3. 验证原密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "原密码不正确");
        }

        // 4. 新密码格式校验
        if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新密码格式不正确，应为6-20字符");
        }

        // 5. 确认密码校验
        if (!newPassword.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的新密码不一致");
        }

        // 8. 检查新密码不能与原密码相同
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新密码不能与原密码相同");
        }

        // 9. 加密新密码
        String encryptNewPassword = passwordEncoder.encode(newPassword);

        // 10. 更新密码
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPassword(encryptNewPassword);
        updateUser.setUpdatedTime(new Date());

        boolean updateResult = this.updateById(updateUser);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "密码修改失败，数据库异常");
        }

        log.info("用户修改密码成功，用户ID：{}", userId);
        return true;
    }

    @Override
    public UserVO getUserPublicInfo(Long userId) {
        // 1. 参数校验
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }

        // 2. 查询用户信息
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        // 3. 检查用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已被禁用");
        }

        // 4. 转换为公开信息VO
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setCreatedTime(user.getCreatedTime());

        // 5. 设置统计信息
        userVO.setFollowingCount(followService.getFollowingCount(userId));
        userVO.setFollowerCount(followService.getFollowerCount(userId));
        // TODO: 设置文章数量，需要文章服务
        userVO.setArticleCount(0);

        return userVO;
    }

    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            return null;
        }
        return currentUser;
    }

    /**
     * 获取客户端IP地址
     *
     * @param request HTTP请求对象
     * @return 客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotBlank(xForwardedFor) && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.isNotBlank(xRealIp) && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        String proxyClientIp = request.getHeader("Proxy-Client-IP");
        if (StringUtils.isNotBlank(proxyClientIp) && !"unknown".equalsIgnoreCase(proxyClientIp)) {
            return proxyClientIp;
        }

        String wlProxyClientIp = request.getHeader("WL-Proxy-Client-IP");
        if (StringUtils.isNotBlank(wlProxyClientIp) && !"unknown".equalsIgnoreCase(wlProxyClientIp)) {
            return wlProxyClientIp;
        }

        return request.getRemoteAddr();
    }

    @Override
    public User getCurrentLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 获取当前登录用户
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;

        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        }

        // 从数据库查询最新的用户信息
        Long userId = currentUser.getId();
        User user = this.getById(userId);

        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户不存在");
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "账号已被禁用");
        }

        return user;
    }

    // ==================== 管理员用户管理接口实现 ====================

    @Override
    public PageVO<AdminUserVO> listUsersByAdmin(UserQueryRequest userQueryRequest) {
        // 1. 参数校验
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 2. 构建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        // 关键词搜索（用户名、邮箱、昵称）
        String keyword = userQueryRequest.getKeyword();
        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like("username", keyword)
                .or()
                .like("email", keyword)
                .or()
                .like("nickname", keyword)
            );
        }

        // 状态筛选
        Integer status = userQueryRequest.getStatus();
        if (status != null) {
            queryWrapper.eq("status", status);
        }

        // 角色筛选
        String role = userQueryRequest.getRole();
        if (StrUtil.isNotBlank(role)) {
            queryWrapper.eq("role", role);
        }

        // 时间范围筛选
        if (userQueryRequest.getStartTime() != null) {
            queryWrapper.ge("created_time", userQueryRequest.getStartTime());
        }
        if (userQueryRequest.getEndTime() != null) {
            queryWrapper.le("created_time", userQueryRequest.getEndTime());
        }

        // 排序
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        if (StrUtil.isNotBlank(sortField)) {
            if ("asc".equals(sortOrder)) {
                queryWrapper.orderByAsc(sortField);
            } else {
                queryWrapper.orderByDesc(sortField);
            }
        } else {
            // 默认按创建时间倒序
            queryWrapper.orderByDesc("created_time");
        }

        // 3. 分页查询
        Page<User> page = new Page<>(userQueryRequest.getCurrent(), userQueryRequest.getSize());
        Page<User> userPage = this.page(page, queryWrapper);

        // 4. 转换为VO
        List<AdminUserVO> adminUserVOList = userPage.getRecords().stream()
            .map(this::convertToAdminUserVO)
            .collect(Collectors.toList());

        // 5. 构建分页结果
        return new PageVO<>(adminUserVOList, userPage.getTotal(),
            userPage.getCurrent(), userPage.getSize());
    }

    @Override
    public Boolean updateUserStatus(Long userId, UserStatusUpdateRequest userStatusUpdateRequest, HttpServletRequest request) {
        // 1. 参数校验
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        if (userStatusUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        Integer status = userStatusUpdateRequest.getStatus();
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户状态参数错误");
        }

        // 2. 获取当前操作用户
        User currentUser = getCurrentLoginUser(request);
        log.info("管理员 {} 更新用户 {} 状态为 {}, 原因: {}",
            currentUser.getId(), userId, status, userStatusUpdateRequest.getReason());

        // 3. 检查目标用户是否存在
        User targetUser = this.getById(userId);
        if (targetUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "目标用户不存在");
        }

        // 4. 不能操作自己
        if (userId.equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能修改自己的状态");
        }

        // 5. 不能操作其他管理员
        if (UserConstant.ADMIN_ROLE.equals(targetUser.getRole()) && !userId.equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "不能修改其他管理员的状态");
        }

        // 6. 更新用户状态
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setStatus(status);
        updateUser.setUpdatedTime(new Date());

        boolean result = this.updateById(updateUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新用户状态失败");
        }

        return true;
    }

    @Override
    public Boolean updateUserRole(Long userId, UserRoleUpdateRequest userRoleUpdateRequest, HttpServletRequest request) {
        // 1. 参数校验
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        if (userRoleUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        String role = userRoleUpdateRequest.getRole();
        if (StrUtil.isBlank(role)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户角色不能为空");
        }

        // 验证角色值
        if (!UserConstant.DEFAULT_ROLE.equals(role) &&
            !UserConstant.ADMIN_ROLE.equals(role) &&
            !UserConstant.BAN_ROLE.equals(role)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户角色参数错误");
        }

        // 2. 获取当前操作用户
        User currentUser = getCurrentLoginUser(request);
        log.info("管理员 {} 更新用户 {} 角色为 {}", currentUser.getId(), userId, role);

        // 3. 检查目标用户是否存在
        User targetUser = this.getById(userId);
        if (targetUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "目标用户不存在");
        }

        // 4. 不能操作自己
        if (userId.equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能修改自己的角色");
        }

        // 5. 不能操作其他管理员
        if (UserConstant.ADMIN_ROLE.equals(targetUser.getRole()) && !userId.equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "不能修改其他管理员的角色");
        }

        // 6. 更新用户角色
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setRole(role);
        updateUser.setUpdatedTime(new Date());

        boolean result = this.updateById(updateUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新用户角色失败");
        }

        return true;
    }

    @Override
    public Boolean deleteUser(Long userId, HttpServletRequest request) {
        // 1. 参数校验
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }

        // 2. 获取当前操作用户
        User currentUser = getCurrentLoginUser(request);
        log.info("管理员 {} 删除用户 {}", currentUser.getId(), userId);

        // 3. 检查目标用户是否存在
        User targetUser = this.getById(userId);
        if (targetUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "目标用户不存在");
        }

        // 4. 不能删除自己
        if (userId.equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能删除自己");
        }

        // 5. 不能删除其他管理员
        if (UserConstant.ADMIN_ROLE.equals(targetUser.getRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "不能删除管理员账号");
        }

        // 6. 逻辑删除用户
        boolean result = this.removeById(userId);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除用户失败");
        }

        return true;
    }

    /**
     * 转换User实体为AdminUserVO
     *
     * @param user 用户实体
     * @return AdminUserVO
     */
    private AdminUserVO convertToAdminUserVO(User user) {
        if (user == null) {
            return null;
        }

        AdminUserVO adminUserVO = new AdminUserVO();
        BeanUtils.copyProperties(user, adminUserVO);
        adminUserVO.setCreatedTime(user.getCreatedTime());
        adminUserVO.setUpdatedTime(user.getUpdatedTime());

        // 设置统计信息
        adminUserVO.setFollowingCount(followService.getFollowingCount(user.getId()));
        adminUserVO.setFollowerCount(followService.getFollowerCount(user.getId()));
        // TODO: 设置文章数量，需要文章服务
        adminUserVO.setArticleCount(0);

        return adminUserVO;
    }

}
