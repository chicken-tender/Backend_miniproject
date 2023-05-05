package com.kh.Backend_miniproject.controller;
import com.kh.Backend_miniproject.dao.MatchingDAO;
import com.kh.Backend_miniproject.vo.MembersVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class MatchingController {
    // ✅매칭 조건에 맞는 멘토 요청에 대한 응답
    @PostMapping("/mentor")
    public ResponseEntity<List<MembersVO>> fetchMentorInfo(@RequestBody Map<String, Integer> menteeData) {
        int menteeMemberNum = menteeData.get("menteeMemberNum");
        MatchingDAO mdao = new MatchingDAO();
        int mentorMemberNum = mdao.getMentorMemberNum(menteeMemberNum);
        List<MembersVO> mentorInfo = mdao.getMentorInfoByMemberNum(mentorMemberNum);

        if(mentorInfo.isEmpty()) {
            return new ResponseEntity<>(mentorInfo, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(mentorInfo, HttpStatus.OK);
        }
    }

    // ✅멘티 회원번호 요청에 대한 응답
    @PostMapping("/mentee-memberNum")
    public ResponseEntity<Integer> fetchMenteeMemberNum(@RequestBody Map<String, String> menteeData) {
        String menteeEmail = menteeData.get("menteeEmail");
        MatchingDAO mdao = new MatchingDAO();
        int menteeMemberNum = mdao.getMenteeMemberNumByEmail(menteeEmail);

        if (menteeMemberNum == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(menteeMemberNum, HttpStatus.OK);
        }
    }

    // ✅매칭 성공에 따른 회원 번호, 멘티 프로필 사진, 닉네임 요청에 대한 응답
    @PostMapping("/mentee")
    public ResponseEntity<List<MembersVO>> fetchMenteeInfo(@RequestBody Map<String, String> emailData) {
        String menteeEmail = emailData.get("menteeEmail");
        MatchingDAO mdao = new MatchingDAO();
        List<MembersVO> menteeInfo = mdao.getMenteeInfoByEmail(menteeEmail);

        if(menteeInfo == null) {
            return new ResponseEntity<>(menteeInfo, HttpStatus.NOT_FOUND);
        } return new ResponseEntity<>(menteeInfo, HttpStatus.OK);
    }

    // 🙏사용자의 회원 번호를 가진 멘토, 멘티 있는지 확인 요청에 따른 응답
    @PostMapping("/checkUserMatched")
    public ResponseEntity<Boolean> checkUserMatched(@RequestBody Map<String, Integer> data) {
        int memberNum = data.get("memberNum");
        MatchingDAO mdao = new MatchingDAO();
        boolean isMatched = mdao.isUserMatched(memberNum);

        if(isMatched) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
    }
}
