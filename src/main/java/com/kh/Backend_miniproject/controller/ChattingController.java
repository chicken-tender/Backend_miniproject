package com.kh.Backend_miniproject.controller;
import com.kh.Backend_miniproject.dao.ChattingDAO;
import com.kh.Backend_miniproject.dao.MainDao;
import com.kh.Backend_miniproject.dao.MatchingDAO;
import com.kh.Backend_miniproject.vo.*;
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
    // ✅채팅 시작 요청에 대한 응답 (채팅방 저장)
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

    // ✅채팅 메시지 전송
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

    // ✅매칭된 모든 회원 번호 전송
    @GetMapping("/mentor-mentee")
    public ResponseEntity<List<MentorMenteeVO>> fetchAllMentorMenteeNum() {
        ChattingDAO cdao = new ChattingDAO();
        List<MentorMenteeVO> list = cdao.getAllMentorMenteeNum();

        if(list == null || list.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // ✅로그인 한 유저가 속한 채팅방 요청에 대한 응답
    @GetMapping("/chat/{memberNum}/room")
    public ResponseEntity<Integer> fetchChatRoomByMemberNum(@PathVariable int memberNum) {
        ChattingDAO cdao = new ChattingDAO();
        int chatRoom = cdao.getChatRoomByMemberNum(memberNum);

        if(chatRoom != 0) {
            return new ResponseEntity<>(chatRoom, HttpStatus.OK);
        } return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // ✅채팅방 번호 전달 -> 해당 채팅방 메시지 정보 응답
    @PostMapping("/chat/messages")
    public ResponseEntity<List<ChatMessagesVO>> fetchChatMessages(@RequestBody Map<String, Integer> chatRoom) {
        int chatNum = chatRoom.get("chatRoom");
        ChattingDAO cdao = new ChattingDAO();
        List<ChatMessagesVO> list = cdao.getMessagesByChatNum(chatNum);
        if(list != null) {
            return new ResponseEntity<>(list, HttpStatus.OK);
        } return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // ✅특정 채팅방 유저 정보 응답
    @GetMapping("/chat/chatRoom/{chatNum}")
    public ResponseEntity<List<ChatRoomVO>> fetchChatInfoByChatNum(@PathVariable("chatNum") int chatNumber) {
        ChattingDAO cdao = new ChattingDAO();
        List<ChatRoomVO> list = cdao.getChatInfoByChatNum(chatNumber);

        if(list != null && !list.isEmpty()) {
            return new ResponseEntity<>(list, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // ✅채팅 상대방 회원 정보 요청에 따른 응답
    @GetMapping("/chat/{memberNum}/details")
    public ResponseEntity<UserDetailVO> fetchUserDetailsByMemberNum(@PathVariable("memberNum") int memberNum) {
        ChattingDAO cdao = new ChattingDAO();
        UserDetailVO userDetails = cdao.getUserDetailsByMemberNum(memberNum);

        if(userDetails == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else return new ResponseEntity<>(userDetails, HttpStatus.OK);
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
        cdao.deleteChatMessages(chatNum);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
