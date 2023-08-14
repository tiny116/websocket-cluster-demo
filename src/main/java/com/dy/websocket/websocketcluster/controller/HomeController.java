package com.dy.websocket.websocketcluster.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ding.yi create at 2023/7/26 15:42
 */
@RestController
public class HomeController {

    @Value("${server.port}")
    private String port;

    @GetMapping("getPort")
    public Map<String, Object> getPort() {
        Map<String, Object> map = new HashMap<>();
        map.put("port", port);
        return map;
    }

}
