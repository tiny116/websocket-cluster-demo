package com.dy.websocket.websocketcluster.controller;

import com.alibaba.fastjson2.JSON;
import com.dy.websocket.websocketcluster.model.ChatMessage;
import com.dy.websocket.websocketcluster.service.ChatService;
import com.dy.websocket.websocketcluster.util.JsonUtil;
import com.rabbitmq.tools.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Slf4j
@Controller
public class ChatController {

    @Value("${spring.redis.channel.msgToAll}")
    private String msgToAll;

    @Value("${server.port}")
    private String port;

    @Value("${spring.redis.set.onlineUsers}")
    private String onlineUsers;

    @Value("${spring.redis.channel.userStatus}")
    private String userStatus;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setPort(port);
        log.info("[{}]Receive client msg: {}", port, JsonUtil.parseObjToJson(chatMessage));
        redisTemplate.convertAndSend(msgToAll, JsonUtil.parseObjToJson(chatMessage));
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        chatMessage.setPort(port);
        // Add username in web socket session
        log.info("[{}]Receive add user: {}", port, JsonUtil.parseObjToJson(chatMessage));
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        redisTemplate.opsForSet().add(onlineUsers, chatMessage.getSender());
        redisTemplate.convertAndSend(userStatus, JsonUtil.parseObjToJson(chatMessage));
    }
}