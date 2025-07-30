package com.sujiu.blog.controller;

import com.sujiu.blog.annotation.RequireLogin;
import com.sujiu.blog.common.BaseResponse;
import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.common.ResultUtils;
import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.model.vo.file.FileUploadVO;
import com.sujiu.blog.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 文件上传控制器
 *
 * @author sujiu
 */
@RestController
@RequestMapping("/upload")
@Slf4j
@Tag(name = "文件上传", description = "文件上传相关接口")
public class FileUploadController {

    @Resource
    private FileUploadService fileUploadService;

    /**
     * 上传图片
     *
     * @param file 图片文件
     * @param type 上传类型（avatar, cover, content）
     * @param request HTTP请求对象
     * @return 文件上传结果
     */
    @Operation(summary = "上传图片", description = "上传图片文件，支持头像、封面、内容图片等类型")
    @RequireLogin
    @PostMapping("/image")
    public BaseResponse<FileUploadVO> uploadImage(
            @Parameter(description = "图片文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "上传类型：avatar-头像，cover-封面，content-内容图片") 
            @RequestParam("type") String type,
            HttpServletRequest request) {
        
        log.info("收到图片上传请求，类型：{}，文件名：{}", type, file.getOriginalFilename());

        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择要上传的图片文件");
        }

        FileUploadVO result = fileUploadService.uploadImage(file, type, request);
        return ResultUtils.success(result, "图片上传成功");
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @param type 上传类型（document, attachment）
     * @param request HTTP请求对象
     * @return 文件上传结果
     */
    @Operation(summary = "上传文件", description = "上传文档文件，支持PDF、Word、Excel等格式")
    @RequireLogin
    @PostMapping("/file")
    public BaseResponse<FileUploadVO> uploadFile(
            @Parameter(description = "文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "上传类型：document-文档，attachment-附件") 
            @RequestParam("type") String type,
            HttpServletRequest request) {
        
        log.info("收到文件上传请求，类型：{}，文件名：{}", type, file.getOriginalFilename());

        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择要上传的文件");
        }

        FileUploadVO result = fileUploadService.uploadFile(file, type, request);
        return ResultUtils.success(result, "文件上传成功");
    }
}
