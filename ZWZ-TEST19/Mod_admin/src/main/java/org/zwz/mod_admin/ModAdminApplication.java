package org.zwz.mod_admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("org.zwz.mod_admin.mapper")
public class ModAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModAdminApplication.class, args);
    }

}
