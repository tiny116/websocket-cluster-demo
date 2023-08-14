package com.dy.websocket.websocketcluster.listener;

import com.alibaba.fastjson.JSONObject;
import com.dy.websocket.websocketcluster.model.ChatMessage;
import com.dy.websocket.websocketcluster.service.ChatService;
import com.dy.websocket.websocketcluster.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author ding.yi create at 2023/7/27 15:45
 */
@Slf4j
@Component
public class RedisListener extends MessageListenerAdapter {

    @Value("${server.port}")
    private String port;

    @Value("${spring.redis.channel.msgToAll}")
    private String msgToAll;

    @Value("${spring.redis.channel.userStatus}")
    private String userStatus;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private ChatService chatService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        byte[] body = message.getBody();
        byte[] channel = message.getChannel();
        String rawMsg;
        String topic;

        try {
            rawMsg = redisTemplate.getStringSerializer().deserialize(body);
            topic = redisTemplate.getStringSerializer().deserialize(channel);
            log.info("[{}]Received raw message from topic: {}, raw message content: {}", port, topic, rawMsg);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return;
        }

        ChatMessage chatMessage = JsonUtil.parseJsonToObj(rawMsg, ChatMessage.class);

        if (msgToAll.equals(topic)) {
            log.info("[{}]Send message to all users: {}", port,  rawMsg);
            chatService.sendMsg(chatMessage);
        } else if (userStatus.equals(topic)) {
            if (null != chatMessage) {
                chatService.alertUserStatus(chatMessage);
            }
        }
    }
}
