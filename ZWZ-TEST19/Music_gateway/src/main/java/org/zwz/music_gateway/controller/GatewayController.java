package org.zwz.music_gateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gateway")
public class GatewayController {
    @Value("${spring.application.name}")
    private String applicationName;

    @RequestMapping("/test")
    public String test() {
        System.out.println("test + " + applicationName);
        return "test + " + applicationName;
    }
}