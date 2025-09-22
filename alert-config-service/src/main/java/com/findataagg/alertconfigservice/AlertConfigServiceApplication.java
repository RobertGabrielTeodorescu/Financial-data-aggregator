package com.findataagg.alertconfigservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.findataagg.alertconfigservice",
        "com.findataagg.common",
        "com.findataagg.alert"
})
public class AlertConfigServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlertConfigServiceApplication.class, args);
    }
}
