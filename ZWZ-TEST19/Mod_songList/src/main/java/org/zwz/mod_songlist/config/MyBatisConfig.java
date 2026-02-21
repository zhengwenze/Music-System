package org.zwz.mod_songlist.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 */
@Configuration
@MapperScan(basePackages = "org.zwz.mod_songlist.mapper")
public class MyBatisConfig {
    // 直接扫描当前模块的mapper包
}