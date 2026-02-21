package org.zwz.mod_upload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ModUploadApplication {
    public static void main(String[] args) {
        SpringApplication.run(ModUploadApplication.class, args);
    }
}