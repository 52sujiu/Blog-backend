package com.sujiu.blog.common;

import com.sujiu.blog.model.vo.common.PageVO;
import java.util.List;

/**
 * 返回工具类
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public class ResultUtils {

    /**
     * 成功
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "success");
    }

    /**
     * 成功（自定义消息）
     *
     * @param data
     * @param message
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data, String message) {
        return new BaseResponse<>(0, data, message);
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     *
     * @param code
     * @param message
     * @return
     */
    public static BaseResponse error(int code, String message) {
        return new BaseResponse(code, null, message);
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String message) {
        return new BaseResponse(errorCode.getCode(), null, message);
    }

    /**
     * 分页成功响应
     *
     * @param records 数据列表
     * @param total   总记录数
     * @param size    每页大小
     * @param current 当前页码
     * @param <T>     数据类型
     * @return 分页响应
     */
    public static <T> BaseResponse<PageVO<T>> success(List<T> records, long total, long size, long current) {
        PageVO<T> pageVO = new PageVO<>();
        pageVO.setRecords(records);
        pageVO.setTotal(total);
        pageVO.setSize(size);
        pageVO.setCurrent(current);
        pageVO.setPages(size > 0 ? (total + size - 1) / size : 0L);
        return new BaseResponse<>(0, pageVO, "success");
    }

    /**
     * 分页成功响应
     *
     * @param pageVO 分页数据
     * @param <T>    数据类型
     * @return 分页响应
     */
    public static <T> BaseResponse<PageVO<T>> success(PageVO<T> pageVO) {
        return new BaseResponse<>(0, pageVO, "success");
    }
}
