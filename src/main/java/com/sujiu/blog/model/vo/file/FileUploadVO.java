package com.sujiu.blog.model.vo.file;

import java.io.Serializable;
import lombok.Data;

/**
 * 文件上传响应
 *
 * @author sujiu
 */
@Data
public class FileUploadVO implements Serializable {

    /**
     * 文件访问URL
     */
    private String url;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 文件大小（字节）
     */
    private Long size;

    /**
     * 文件MIME类型
     */
    private String type;

    private static final long serialVersionUID = 1L;
}
