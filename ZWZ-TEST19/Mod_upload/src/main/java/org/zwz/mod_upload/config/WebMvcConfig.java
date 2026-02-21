package org.zwz.mod_upload.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${service.download.address}")
    private String downloadAddress;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置文件访问路径映射
        // 确保路径格式正确，对于Windows系统，使用正确的路径分隔符
        String location = "file:" + downloadAddress;
        
        // 配置从/file/**到实际存储目录的映射
        registry.addResourceHandler("/file/**")
                .addResourceLocations(location)
                .setCachePeriod(0); // 禁用缓存便于调试
    }
}