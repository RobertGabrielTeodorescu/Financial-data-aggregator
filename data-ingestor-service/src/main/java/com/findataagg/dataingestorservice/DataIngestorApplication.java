package com.findataagg.dataingestorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"com.findataagg.dataingestorservice", "com.findataagg.common"})
@EnableConfigurationProperties
public class DataIngestorApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(DataIngestorApplication.class, args);

        // This line will block the main thread, keeping the application alive
        // so the WebSocket's background threads can continue listening for messages.
        Thread.currentThread().join();
    }

}
