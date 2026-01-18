package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableFeignClients(basePackages = "ru.practicum.client")
public class MainStats {
    public static void main(String[] args) {
        SpringApplication.run(MainStats.class, args);
    }
}
