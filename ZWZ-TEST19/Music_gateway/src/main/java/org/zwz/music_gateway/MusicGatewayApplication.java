package org.zwz.music_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
// 移除所有过滤器相关配置，让Spring Boot自动扫描但不包括过滤器
public class MusicGatewayApplication {

    public static void main(String[] args) {
        // 启动网关应用，禁用所有自定义过滤器
        SpringApplication.run(MusicGatewayApplication.class, args);
    }

}