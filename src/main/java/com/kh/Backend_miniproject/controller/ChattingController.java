package com.kh.Backend_miniproject.controller;
import com.kh.Backend_miniproject.dao.ChattingDAO;
import com.kh.Backend_miniproject.dao.MatchingDAO;
import com.kh.Backend_miniproject.vo.ChatMessagesVO;
import com.kh.Backend_miniproject.vo.MembersVO;
import com.kh.Backend_miniproject.vo.UserDetailVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class ChattingController {
    // 🏓채팅 시작 요청에 대한 응답 (채팅방 저장)
    @PostMapping("/chat")
    public ResponseEntity<Integer> saveChatRoom(@RequestBody Map<String, Integer> memberNumData) {
        System.out.println("memberNumData: " + memberNumData);
        int mentorMemberNum = memberNumData.get("mentorMemberNum");
        int menteeMemberNum = memberNumData.get("menteeMemberNum");
        ChattingDAO cdao = new ChattingDAO();
        Integer result = cdao.createChatRoom(mentorMemberNum, menteeMemberNum);

        if(result != null) {
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // 🏓채팅 메시지 전송
    @PostMapping("/chat/message")
    public ResponseEntity<Boolean> sendChatMessage(@RequestBody Map<String, Object> data) {
        ChattingDAO cdao = new ChattingDAO();

        int chatNum = (Integer) data.get("chatNum");
        int senderId = (Integer) data.get("senderId");
        int receiverId = (Integer) data.get("receiverId");
        String message = (String) data.get("message");
        String codeBlock = (String) data.get("codeBlock");
        int messageType = (Integer) data.get("messageType");

        String createdAtStr = (String) data.get("createdAt");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        LocalDateTime createdAtLdt = LocalDateTime.parse(createdAtStr, formatter);
        Timestamp createdAt = Timestamp.valueOf(createdAtLdt);
        Character isRead = ((String) data.get("isRead")).charAt(0);

        boolean result = cdao.saveChatMessage(chatNum, senderId, receiverId, message, codeBlock, messageType, createdAt, isRead);

        if(result) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }

    // 🏓채팅 메시지 조회
    @GetMapping("/chat/messages/{senderId}/{receiverId}")
    public ResponseEntity<List<ChatMessagesVO>> fetchChatMessages(@PathVariable int senderId, @PathVariable int receiverId) {
        ChattingDAO cdao = new ChattingDAO();
        List<ChatMessagesVO> list = cdao.getChatMessages(senderId, receiverId);

        if(list == null) {
            return new ResponseEntity<>(list, HttpStatus.NOT_FOUND);
        } return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // 🏓안읽은 메시지 조회
    @GetMapping("/chat/{userId}/unread-messages")
    public ResponseEntity<List<ChatMessagesVO>> fetchUnreadMessages(@PathVariable int memberNum) {
        ChattingDAO cdao = new ChattingDAO();
        List<ChatMessagesVO> list = cdao.getUnreadMessages(memberNum);

        if(list == null) {
            return new ResponseEntity<>(list, HttpStatus.NOT_FOUND);
        } return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // 🏓️메시지 읽었다고 알리기
    @PatchMapping("/chat/message/{messageId}")
    public ResponseEntity<Boolean> updateMessageReadStatus(@PathVariable("messageId") int messageId) {
        ChattingDAO cdao = new ChattingDAO();
        boolean result = cdao.markMessageAsRead(messageId);

        if(result) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }

    // 🏓대화 종료 요청에 따른 대화방 삭제
    @DeleteMapping("/chat")
    public ResponseEntity<Void> deleteChatRoom(@RequestParam int chatNum) {
        ChattingDAO cdao = new ChattingDAO();
        cdao.deleteChatRoom(chatNum);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 🏓대화 종료 요청에 따른 채팅 메시지 삭제
    @DeleteMapping("/chat/messages")
    public ResponseEntity<Void> deleteChatMessages(@RequestParam int chatNum) {
        ChattingDAO cdao = new ChattingDAO();
        cdao.deleteChatRoom(chatNum);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 🏓채팅 상대방 회원 정보 요청에 따른 응답
    @GetMapping("/chat/{memberNum}/details")
    public ResponseEntity<UserDetailVO> fetchUserDetailsByMemberNum(@PathVariable("memberNum") int memberNum) {
        ChattingDAO cdao = new ChattingDAO();
        UserDetailVO userDetails = cdao.getUserDetailsByMemberNum(memberNum);

        if(userDetails == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else return new ResponseEntity<>(userDetails, HttpStatus.OK);
    }
}
