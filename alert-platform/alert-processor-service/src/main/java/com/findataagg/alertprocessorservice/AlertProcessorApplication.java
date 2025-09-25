package com.findataagg.alertprocessorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.findataagg.alertprocessorservice", "com.findataagg.common", "com.findataagg.alert"})
class AlertProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlertProcessorApplication.class, args);
    }

}
