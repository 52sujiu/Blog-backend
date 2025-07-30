package com.sujiu.blog.service;

import com.sujiu.blog.model.vo.file.FileUploadVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * 文件上传服务
 *
 * @author sujiu
 */
public interface FileUploadService {

    /**
     * 上传图片
     *
     * @param file 图片文件
     * @param type 上传类型（avatar, cover, content）
     * @param request HTTP请求对象
     * @return 文件上传结果
     */
    FileUploadVO uploadImage(MultipartFile file, String type, HttpServletRequest request);

    /**
     * 上传文件
     *
     * @param file 文件
     * @param type 上传类型（document, attachment）
     * @param request HTTP请求对象
     * @return 文件上传结果
     */
    FileUploadVO uploadFile(MultipartFile file, String type, HttpServletRequest request);
}
