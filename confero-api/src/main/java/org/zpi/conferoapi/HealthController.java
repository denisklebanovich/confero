package org.zpi.conferoapi;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {
    @Value("${MAIL_PASS}")
    private String myEnvVar;

    @GetMapping
    public String health() {
        return "OK";
    }


    @PostConstruct
    public void printEnvVar() {
        System.out.println("MY_ENV_VAR: " + myEnvVar);
    }
}
