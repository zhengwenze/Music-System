package org.zwz.mod_like;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

//不准修改启动类ModLikeApplication
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("org.zwz.mod_like.mapper")
public class ModLikeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModLikeApplication.class, args);
    }

}
