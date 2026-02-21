package org.zwz.mod_setting;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//启动类，不要随便改动
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "org.zwz.mod_setting")
@MapperScan(basePackages = "org.zwz.mod_setting.mapper")
public class ModSettingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModSettingApplication.class, args);
    }

}