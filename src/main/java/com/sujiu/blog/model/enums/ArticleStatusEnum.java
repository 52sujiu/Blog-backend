package com.sujiu.blog.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章状态枚举
 *
 * @author sujiu
 */
public enum ArticleStatusEnum {

    DRAFT("草稿", 0),
    REVIEWING("审核中", 1),
    PUBLISHED("已发布", 2),
    REJECTED("已拒绝", 3),
    OFFLINE("已下架", 4);

    private final String text;

    private final Integer value;

    ArticleStatusEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static ArticleStatusEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (ArticleStatusEnum anEnum : ArticleStatusEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
