package org.zwz.mod_msg;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "org.zwz.mod_msg")
@MapperScan(basePackages = "org.zwz.mod_msg.mapper")
public class ModMsgApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModMsgApplication.class, args);
    }

}