package com.dy.websocket.websocketcluster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = {"com.dy.websocket.websocketcluster"})
public class WebsocketClusterApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebsocketClusterApplication.class, args);
    }

}
