package com.dy.websocket.websocketcluster.listener;

import com.dy.websocket.websocketcluster.enumeration.MessageType;
import com.dy.websocket.websocketcluster.model.ChatMessage;
import com.dy.websocket.websocketcluster.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.annotation.Resource;

@Slf4j
@Component
public class WebSocketEventListener {

    @Value("${server.port}")
    private String port;

    @Value("${spring.redis.set.onlineUsers}")
    private String onlineUsers;

    @Value("${spring.redis.channel.userStatus}")
    private String userStatus;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("[{}]Received a new web socket connection", port);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username != null) {
            log.info("[{}]User Disconnected : {}", port, username);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(MessageType.LEAVE);
            chatMessage.setSender(username);

            redisTemplate.opsForSet().remove(onlineUsers, chatMessage.getSender());
            redisTemplate.convertAndSend(userStatus, JsonUtil.parseObjToJson(chatMessage));
        }
    }
}