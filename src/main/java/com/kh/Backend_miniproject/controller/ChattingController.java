package com.kh.Backend_miniproject.controller;
import com.kh.Backend_miniproject.dao.ChattingDAO;
import com.kh.Backend_miniproject.vo.MembersVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class ChattingController {
    // 🏓매칭 조건에 맞는 멘토 요청에 대한 응답
    @PostMapping("/mentor")
    public ResponseEntity<List<MembersVO>> fetchMentorInfo(@RequestBody Map<String, Integer> menteeData) {
        int menteeMemberNum = menteeData.get("menteeMemberNum");
        ChattingDAO cdao = new ChattingDAO();
        int mentorMemberNum = cdao.getMentorMemberNum(menteeMemberNum);
        List<MembersVO> mentorInfo = cdao.getMentorInfoByMemberNum(mentorMemberNum);

        if(mentorInfo.isEmpty()) {
            return new ResponseEntity<>(mentorInfo, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(mentorInfo, HttpStatus.OK);
        }
    }

    // 🏓매칭 성공에 따른 멘티 프로필 사진, 닉네임 요청에 대한 응답
    @PostMapping("/mentee")
    public ResponseEntity<List<MembersVO>> fetchMenteeInfo(@RequestBody Map<String, String> emailData) {
        String menteeEmail = emailData.get("menteeEmail");
        ChattingDAO cdao = new ChattingDAO();
        List<MembersVO> menteeInfo = cdao.getMenteeInfoByEmail(menteeEmail);

        if(menteeInfo == null) {
            return new ResponseEntity<>(menteeInfo, HttpStatus.NOT_FOUND);
        } return new ResponseEntity<>(menteeInfo, HttpStatus.OK);

    }

    // 🏓채팅 시작 요청에 대한 응답 (채팅방 저장)
    @PostMapping("/chat")
    public ResponseEntity<Boolean> saveChatRoom(@RequestBody Map<String, Integer> memberNumData) {
        int mentorMemberNum = memberNumData.get("mentorMemberNum");
        int menteeMemberNum = memberNumData.get("menteeMemberNum");
        ChattingDAO cdao = new ChattingDAO();
        boolean result = cdao.createChatRoom(mentorMemberNum, menteeMemberNum);

        if(result) {
            return new ResponseEntity<>(true, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }

    // 🏓채팅 칠 때마다 메시지 저장
    @PostMapping("/chat/message")
    public ResponseEntity<Void> saveChatMessage(@RequestBody Map<String, Object> messageData) {
        int chatNum = (Integer) messageData.get("chatNum");
        int senderId = (Integer) messageData.get("senderId");
        String message = (String) messageData.get("message");
        String codeBlock = (String) messageData.get("codeBlock");
        int msgType = (Integer) messageData.get("msgType");

        ChattingDAO cdao = new ChattingDAO();
        cdao.saveChatMessage(chatNum, senderId, message, codeBlock, msgType);

        return new ResponseEntity<>(HttpStatus.CREATED);
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
}
