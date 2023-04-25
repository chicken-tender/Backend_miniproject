package com.kh.Backend_miniproject.controller;
import com.kh.Backend_miniproject.dao.ChattingDAO;
import com.kh.Backend_miniproject.vo.MembersVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class ChattingController {
    // 🏓매칭 조건에 맞는 멘토 요청에 대한 응답
    @PostMapping("/mentor")
    public ResponseEntity<List<MembersVO>> fetchMentorProfile(@RequestBody Map<String, Integer> menteeData) {
        int menteeMemberNum = menteeData.get("menteeMemberNum");
        ChattingDAO cdao = new ChattingDAO();
        int mentorMemberNum = cdao.getMentorMemberNum(menteeMemberNum);
        List<MembersVO> mentorProfile = cdao.getMentorProfileByMemberNum(mentorMemberNum);

        if(mentorProfile.isEmpty()) {
            return new ResponseEntity<>(mentorProfile, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(mentorProfile, HttpStatus.OK);
        }
    }

}
