package com.example.tricolv2sb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class TricolV2SbApplication {

    public static void main(String[] args) {
        SpringApplication.run(TricolV2SbApplication.class, args);
    }

}
