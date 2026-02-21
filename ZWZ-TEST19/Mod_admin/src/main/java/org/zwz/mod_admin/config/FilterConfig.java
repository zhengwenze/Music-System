package org.zwz.mod_admin.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * 过滤器配置类 - 用于设置全局字符编码
 */
@Configuration
public class FilterConfig {

    /**
     * 注册字符编码过滤器，确保所有请求和响应都使用UTF-8编码
     */
    @Bean
    public FilterRegistrationBean<CharacterEncodingFilter> customCharacterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        // 设置请求编码为UTF-8
        filter.setEncoding("UTF-8");
        // 设置响应编码为UTF-8
        filter.setForceEncoding(true);
        // 设置过滤器名称和优先级
        FilterRegistrationBean<CharacterEncodingFilter> registrationBean = 
                new FilterRegistrationBean<>(filter);
        // 设置过滤器名称
        registrationBean.setName("customCharacterEncodingFilter");
        // 设置最高优先级
        registrationBean.setOrder(1);
        // 拦截所有请求
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}

