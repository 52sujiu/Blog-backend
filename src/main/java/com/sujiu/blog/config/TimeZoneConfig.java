package com.sujiu.blog.config;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * 时区配置
 *
 * @author sujiu
 */
@Configuration
public class TimeZoneConfig {

    @PostConstruct
    public void init() {
        // 设置应用程序默认时区为北京时间
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }
}
