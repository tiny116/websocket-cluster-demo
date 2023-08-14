package com.dy.websocket.websocketcluster.service;

import com.alibaba.fastjson.JSON;
import com.dy.websocket.websocketcluster.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author ding.yi create at 2023/7/27 15:42
 */
@Slf4j
@Service
public class ChatService {

    @Value("${server.port}")
    private String port;

    @Resource
    private SimpMessageSendingOperations simpMessageSendingOperations;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public void sendMsg(@Payload ChatMessage chatMessage) {
        log.info("[{}]Send by SimpMessageSendingOperations: {}", port, JSON.toJSONString(chatMessage));
        redisTemplate.opsForValue().set("msg:" + port + "_" + System.nanoTime(), JSON.toJSONString(chatMessage));
        simpMessageSendingOperations.convertAndSend("/topic/public", chatMessage);
    }

    public void alertUserStatus(@Payload ChatMessage chatMessage) {
        log.info("[{}]Alert user online by SimpMessageSendingOperations: {}", port, JSON.toJSONString(chatMessage));
        simpMessageSendingOperations.convertAndSend("/topic/public", chatMessage);
    }

}
