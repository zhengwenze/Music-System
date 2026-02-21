package org.zwz.mod_singer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"org.zwz.mod_singer"})
@MapperScan(basePackages = {"org.zwz.mod_singer.mapper"})
public class ModSingerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModSingerApplication.class, args);
    }

}