package com.pdf.indexador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class IndexadorApplication {

    public static void main(String[] args) {
        SpringApplication.run(IndexadorApplication.class, args);
    }
}