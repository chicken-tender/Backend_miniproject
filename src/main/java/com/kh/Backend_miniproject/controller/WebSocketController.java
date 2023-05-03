package com.kh.Backend_miniproject.controller;
import com.kh.Backend_miniproject.vo.ChatRoomVO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class WebSocketController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    /* ✨@MessageMapping annotation은 메시지의 destination이 /hello 였다면 해당 sendMessage() 메소드가 불리도록 해줌.
    *  ✨sendMessage()에서는 simpMessagingTemplate.convertAndSend를 통해 /sub/chat/{chatNum 채널을 구독 중인 클라이언트에게 메시지를 전송한다.
    *  ✨SimpMessagingTemplate는 특정 브로커로 메시지를 전달한다. */
    @MessageMapping("/chat")
    public void sendMessage(ChatRoomVO cvo, SimpMessageHeaderAccessor accessor) {
        simpMessagingTemplate.convertAndSend("/sub/chat/" + cvo.getChatNum(), cvo);
    }
}
