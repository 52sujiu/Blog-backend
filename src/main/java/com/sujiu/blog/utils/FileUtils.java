package com.sujiu.blog.utils;

import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件处理工具类
 *
 * @author sujiu
 */
@Slf4j
public class FileUtils {

    /**
     * 允许的图片文件类型
     */
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    /**
     * 允许的文档文件类型
     */
    private static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList(
            "application/pdf", "application/msword", 
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain", "text/csv"
    );

    /**
     * 最大文件大小（10MB）
     */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 文件上传根目录
     */
    private static final String UPLOAD_ROOT_PATH = "uploads";

    /**
     * 验证图片文件
     *
     * @param file 上传的文件
     */
    public static void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过10MB");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "只支持 JPEG、PNG、GIF、WebP 格式的图片");
        }

        // 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !hasValidImageExtension(originalFilename)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件扩展名不正确");
        }
    }

    /**
     * 验证文档文件
     *
     * @param file 上传的文件
     */
    public static void validateDocumentFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过10MB");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_DOCUMENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的文件类型");
        }
    }

    /**
     * 检查是否为有效的图片扩展名
     *
     * @param filename 文件名
     * @return 是否有效
     */
    private static boolean hasValidImageExtension(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return Arrays.asList("jpg", "jpeg", "png", "gif", "webp").contains(extension);
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 扩展名
     */
    public static String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    /**
     * 生成唯一文件名
     *
     * @param originalFilename 原始文件名
     * @return 唯一文件名
     */
    public static String generateUniqueFilename(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return extension.isEmpty() ? uuid : uuid + "." + extension;
    }

    /**
     * 生成文件保存路径
     *
     * @param type 文件类型（images, documents）
     * @param filename 文件名
     * @return 文件保存路径
     */
    public static String generateFilePath(String type, String filename) {
        LocalDate now = LocalDate.now();
        String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        // 使用绝对路径，避免相对路径问题
        String userHome = System.getProperty("user.home");
        return String.format("%s/%s/%s/%s/%s", userHome, UPLOAD_ROOT_PATH, type, datePath, filename);
    }

    /**
     * 保存文件到本地
     *
     * @param file 上传的文件
     * @param filePath 文件保存路径
     * @return 保存的文件对象
     */
    public static File saveFileToLocal(MultipartFile file, String filePath) {
        try {
            // 创建目录
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());

            // 保存文件
            File targetFile = new File(filePath);
            file.transferTo(targetFile);

            log.info("文件保存成功：{}", filePath);
            return targetFile;
        } catch (IOException e) {
            log.error("文件保存失败：{}", filePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件保存失败");
        }
    }

    /**
     * 生成文件访问URL
     *
     * @param filePath 文件路径
     * @param baseUrl 基础URL
     * @return 文件访问URL
     */
    public static String generateFileUrl(String filePath, String baseUrl) {
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        if (!filePath.startsWith("/")) {
            filePath = "/" + filePath;
        }
        return baseUrl + filePath;
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public static boolean deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                log.info("文件删除{}：{}", deleted ? "成功" : "失败", filePath);
                return deleted;
            }
            return true;
        } catch (Exception e) {
            log.error("文件删除失败：{}", filePath, e);
            return false;
        }
    }

    /**
     * 获取文件MIME类型
     *
     * @param file 文件
     * @return MIME类型
     */
    public static String getContentType(File file) {
        try {
            return Files.probeContentType(file.toPath());
        } catch (IOException e) {
            log.warn("无法获取文件MIME类型：{}", file.getPath());
            return "application/octet-stream";
        }
    }
}
