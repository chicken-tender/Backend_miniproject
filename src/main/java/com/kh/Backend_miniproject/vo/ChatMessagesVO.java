package com.kh.Backend_miniproject.vo;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ChatMessagesVO {
    private int messageNum;
    private int ChatNum;
    private int senderId;
    private int receiverId;
    private String message;
    private String codeBlock;
    private int messageType;
    private Timestamp createdAt;
    private Character isRead;
}
