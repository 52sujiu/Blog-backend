package com.sujiu.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sujiu.blog.model.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统配置Mapper
 *
 * @author sujiu
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {

    /**
     * 根据配置键获取配置值
     *
     * @param configKey 配置键
     * @return 配置值
     */
    @Select("SELECT config_value FROM sys_config WHERE config_key = #{configKey}")
    String getConfigValue(@Param("configKey") String configKey);

    /**
     * 根据配置键列表批量获取配置
     *
     * @param configKeys 配置键列表
     * @return 配置列表
     */
    @Select("<script>" +
            "SELECT * FROM sys_config WHERE config_key IN " +
            "<foreach collection='configKeys' item='key' open='(' separator=',' close=')'>" +
            "#{key}" +
            "</foreach>" +
            "</script>")
    List<SystemConfig> getConfigsByKeys(@Param("configKeys") List<String> configKeys);

    /**
     * 获取所有系统配置
     *
     * @return 系统配置列表
     */
    @Select("SELECT * FROM sys_config WHERE is_system = 1 ORDER BY id")
    List<SystemConfig> getAllSystemConfigs();
}
