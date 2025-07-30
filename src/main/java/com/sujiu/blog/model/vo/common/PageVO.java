package com.sujiu.blog.model.vo.common;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 分页响应
 *
 * @author sujiu
 */
@Data
public class PageVO<T> implements Serializable {

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 每页大小
     */
    private Long size;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 总页数
     */
    private Long pages;

    private static final long serialVersionUID = 1L;

    /**
     * 默认构造函数
     */
    public PageVO() {
    }

    /**
     * 构造函数
     *
     * @param records 数据列表
     * @param total 总记录数
     * @param current 当前页码
     * @param size 每页大小
     */
    public PageVO(List<T> records, Long total, Integer current, Integer size) {
        this.records = records;
        this.total = total;
        this.current = current != null ? current.longValue() : 1L;
        this.size = size != null ? size.longValue() : 10L;
        this.pages = this.size > 0 ? (this.total + this.size - 1) / this.size : 0L;
    }

    /**
     * 构造函数
     *
     * @param records 数据列表
     * @param total 总记录数
     * @param current 当前页码
     * @param size 每页大小
     */
    public PageVO(List<T> records, Long total, Long current, Long size) {
        this.records = records;
        this.total = total;
        this.current = current != null ? current : 1L;
        this.size = size != null ? size : 10L;
        this.pages = this.size > 0 ? (this.total + this.size - 1) / this.size : 0L;
    }
}
