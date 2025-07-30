package com.sujiu.blog.service.impl;

import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.model.entity.User;
import com.sujiu.blog.model.vo.file.FileUploadVO;
import com.sujiu.blog.service.FileUploadService;
import com.sujiu.blog.service.UserService;
import com.sujiu.blog.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 文件上传服务实现
 *
 * @author sujiu
 */
@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {

    @Resource
    private UserService userService;

    /**
     * 服务器基础URL，用于生成文件访问链接
     */
    @Value("${server.base-url:http://localhost:8101}")
    private String baseUrl;

    /**
     * 允许的图片上传类型
     */
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("avatar", "cover", "content");

    /**
     * 允许的文件上传类型
     */
    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList("document", "attachment");

    @Override
    public FileUploadVO uploadImage(MultipartFile file, String type, HttpServletRequest request) {
        log.info("开始上传图片，类型：{}", type);

        // 获取当前登录用户（@RequireLogin注解已确保用户已登录）
        User currentUser = userService.getCurrentLoginUser(request);

        // 验证上传类型
        if (type == null || !ALLOWED_IMAGE_TYPES.contains(type)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的图片上传类型");
        }

        // 验证图片文件
        FileUtils.validateImageFile(file);

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = FileUtils.generateUniqueFilename(originalFilename);

        // 生成文件保存路径
        String filePath = FileUtils.generateFilePath("images", uniqueFilename);

        // 保存文件
        File savedFile = FileUtils.saveFileToLocal(file, filePath);

        // 生成访问URL
        String fileUrl = FileUtils.generateFileUrl(filePath, baseUrl);

        // 构建返回结果
        FileUploadVO result = new FileUploadVO();
        result.setUrl(fileUrl);
        result.setFilename(uniqueFilename);
        result.setOriginalName(originalFilename);
        result.setSize(file.getSize());
        result.setType(file.getContentType());

        log.info("图片上传成功，用户ID：{}，文件名：{}，大小：{}字节", 
                currentUser.getId(), uniqueFilename, file.getSize());

        return result;
    }

    @Override
    public FileUploadVO uploadFile(MultipartFile file, String type, HttpServletRequest request) {
        log.info("开始上传文件，类型：{}", type);

        // 获取当前登录用户（@RequireLogin注解已确保用户已登录）
        User currentUser = userService.getCurrentLoginUser(request);

        // 验证上传类型
        if (type == null || !ALLOWED_FILE_TYPES.contains(type)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的文件上传类型");
        }

        // 验证文档文件
        FileUtils.validateDocumentFile(file);

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = FileUtils.generateUniqueFilename(originalFilename);

        // 生成文件保存路径
        String filePath = FileUtils.generateFilePath("documents", uniqueFilename);

        // 保存文件
        File savedFile = FileUtils.saveFileToLocal(file, filePath);

        // 生成访问URL
        String fileUrl = FileUtils.generateFileUrl(filePath, baseUrl);

        // 构建返回结果
        FileUploadVO result = new FileUploadVO();
        result.setUrl(fileUrl);
        result.setFilename(uniqueFilename);
        result.setOriginalName(originalFilename);
        result.setSize(file.getSize());
        result.setType(file.getContentType());

        log.info("文件上传成功，用户ID：{}，文件名：{}，大小：{}字节", 
                currentUser.getId(), uniqueFilename, file.getSize());

        return result;
    }
}
