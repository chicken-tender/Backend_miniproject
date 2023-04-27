package com.kh.Backend_miniproject.controller;
import com.kh.Backend_miniproject.dao.AccountDAO;
import com.kh.Backend_miniproject.vo.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class AccountController {
    // 📍 POST : 로그인 - 경미 테스트
    @PostMapping("/login")
    public ResponseEntity<Boolean> loginMember(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String pwd = loginData.get("pwd");
        AccountDAO aDao = new AccountDAO();
        boolean result = aDao.isLoginValid(email, pwd);

        if (result) { // 입력한 email,pwd가 유효한 정보이면 'true'와 Http 상태 코드 '200'을 전달
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else { // 입력한 email, pwd가 유효하지 않은 정보이면 'false'와 Http 상태 코드 '401'을 전달
            return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
        }
    }

    // 🔑 마이페이지: 회원정보 조회 (등급아이콘, 총 게시글 수, 총 댓글 수)
    @GetMapping("/members/info")
    public ResponseEntity<List<MyPageVO>> fetchMemberInfoByNum(@RequestParam int memberNum) {
        AccountDAO dao = new AccountDAO();
        List<MyPageVO> list = dao.getMemberInfoByNum(memberNum);
        if (!list.isEmpty()) {
            return new ResponseEntity<>(list, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    // 🔑(마이페이지) 회원 기술 스택
    @GetMapping("/members/tech-stacks")
    public ResponseEntity<List<TechStackVO>> fetchMemberTechStackByNum(@RequestParam int memberNum) {
        AccountDAO dao = new AccountDAO();
        List<TechStackVO> list = dao.getMemberTechStackByNum(memberNum);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // 🔑(마이페이지) 회원의 최근 게시글 5개 (카테고리, 제목, 본문, 날짜)
    @GetMapping("/members/my-5-latest-posts")
    public ResponseEntity<List<MyPageVO>> fetchMyLatestPostsByNum(@RequestParam int memberNum) {
        AccountDAO dao = new AccountDAO();
        List<MyPageVO> list = dao.getMemberLatestPosts(memberNum);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // 🔑(마이페이지) 회원의 최근 댓글 5개 (카테고리, 댓글내용, 게시글 제목, 날짜)
    @GetMapping("/members/my-5-latest-replies")
    public ResponseEntity<List<MyPageVO>> fetchMyLatestRepliesByNum(@RequestParam int memberNum) {
        AccountDAO dao = new AccountDAO();
        List<MyPageVO> list = dao.getMemberLatestReplies(memberNum);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // 🔑(마이페이지 > 내 게시글 관리) 회원의 모든 게시글
    @GetMapping("/members/all-posts")
    public ResponseEntity<List<MyPageVO>> fetchAllMyPosts(@RequestParam int memberNum) {
        AccountDAO dao = new AccountDAO();
        List<MyPageVO> list = dao.getMemberAllPosts(memberNum);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // 🔑(마이페이지 > 내 댓글 관리) 회원의 모든 댓글
    @GetMapping("/members/all-replies")
    public ResponseEntity<List<MyPageVO>> fetchAllMyReplies(@RequestParam int memberNum) {
        AccountDAO dao = new AccountDAO();
        List<MyPageVO> list = dao.getMemberAllReplies(memberNum);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // ❌(마이페이지) 회원정보 기본 (프로필사진, 가입일, 닉네임, 이메일, 직업, 연차)
    @GetMapping("/members/my-page-test")
    public ResponseEntity<List<MembersVO>> fetchMemberInfoTest(@RequestParam int memberNum){
        AccountDAO adao = new AccountDAO();
        List<MembersVO> list = adao.getMemberInfoBasicByNumber(memberNum);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // POST:bust_in_silhouette: 회원 가입 : 기술스택 X
    @PostMapping("/members/new")
    public ResponseEntity<Boolean> memberRegister(@RequestBody Map<String, String> regData) {
        Integer getGradeNumber= Integer.valueOf(regData.get("gradeNumber"));
        String getEmail = regData.get("email");
        String getPwd = regData.get("password");
        String getNickname = regData.get("nickName");
        String getJob = regData.get("job");
        Integer getYear = Integer.valueOf(regData.get("year"));
        String getPfImg = String.valueOf(regData.get("pfImg"));
        AccountDAO dao = new AccountDAO();
        boolean isTrue = dao.createMember(getGradeNumber, getEmail, getPwd, getNickname, getJob, getYear, getPfImg);
        return new ResponseEntity<>(isTrue, HttpStatus.OK);
    }

    // 🔥경미. 회원가입시 생성된 회원번호를 이용해서 기술스택 저장
    @PostMapping("/signup")
    public ResponseEntity<Boolean> signUp(@RequestBody Map<String, Object> request) {
        boolean result = false;
        AccountDAO ado = new AccountDAO();

        int gradeNum = (int) request.get("gradeNum");
        String email = (String) request.get("email");
        String pwd = (String) request.get("pwd");
        String nickname = (String) request.get("nickname");
        String job = (String) request.get("job");
        int year = (int) request.get("year");
        String pfImg = (String) request.get("pfImg");

        result = ado.createMember(gradeNum, email, pwd, nickname, job, year, pfImg);

        if(result) {
            List<Map<String, Object>> techStacks = (List<Map<String, Object>>) request.get("techStacks");
            for (Map<String, Object> stack : techStacks) {
                MemberTechStackVO svo = new MemberTechStackVO();
                int stackNum = (int)stack.get("stackNum");
                svo.setStackNum(stackNum);
                result = ado.createMemberTechStack(email, svo.getStackNum());
                if(!result) {
                    return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
                }
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 🔥경미. 마이페이지 회원정보 조회
    @GetMapping("/mypage")
    public ResponseEntity<SignUpVO> fetchMyPageMemberInfo(@RequestParam("memberNum") int memberNum) {
        AccountDAO ado = new AccountDAO();
        SignUpVO svo = ado.readMemberInfoByNumber(memberNum);

        if(svo != null) {
            List<MemberTechStackVO> techStacks = ado.getMemberTechStack(memberNum);
            svo.setTechStacks(techStacks);
            return new ResponseEntity<>(svo, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

