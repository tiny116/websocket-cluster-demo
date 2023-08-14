package com.dy.websocket.websocketcluster.model;

import com.dy.websocket.websocketcluster.enumeration.MessageType;
import lombok.Data;

@Data
public class ChatMessage {

    private MessageType type;

    private String content;

    private String sender;

    private String port;

}