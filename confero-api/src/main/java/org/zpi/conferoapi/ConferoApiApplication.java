package org.zpi.conferoapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ConferoApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConferoApiApplication.class, args);
    }



}
