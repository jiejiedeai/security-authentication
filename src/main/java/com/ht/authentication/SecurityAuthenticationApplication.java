package com.ht.authentication;

import org.minbox.framework.api.boot.autoconfigure.swagger.annotation.EnableApiBootSwagger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableConfigurationProperties
@EnableApiBootSwagger
@EnableDiscoveryClient
public class SecurityAuthenticationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityAuthenticationApplication.class, args);
    }

}
