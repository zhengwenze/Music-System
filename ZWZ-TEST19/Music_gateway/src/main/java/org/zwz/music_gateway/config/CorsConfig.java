package org.zwz.music_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        // 允许的头信息
        config.addAllowedHeader("*");
        // 允许的请求方式
        config.addAllowedMethod("*");
        // 允许的源地址
        config.addAllowedOrigin("*");
        // 是否允许携带凭证
        config.setAllowCredentials(true);
        // 设置预检请求的缓存时间（秒）
        config.setMaxAge(1800L);
        // 暴露的响应头
        config.addExposedHeader("X-Request-ID");
        config.addExposedHeader("X-Gateway-Version");
        config.addExposedHeader("userId");
        // 设置拦截路径
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}