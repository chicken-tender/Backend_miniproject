package com.kh.Backend_miniproject.controller;
import com.kh.Backend_miniproject.dao.AccountDAO;
import com.kh.Backend_miniproject.dao.ChattingDAO;
import com.kh.Backend_miniproject.dao.MainDao;
import com.kh.Backend_miniproject.vo.MembersVO;
import com.kh.Backend_miniproject.vo.PostInfoVO;
import com.kh.Backend_miniproject.vo.TopWriterVO;
import com.sun.tools.javac.Main;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class MainController {
    // ✅글 작성이 많은 상위 5명의 정보 요청에 따른 응답
    @GetMapping("/member/top-5writers")
    public ResponseEntity<List<TopWriterVO>> fetchTopWritersData() {
        MainDao mdao = new MainDao();
        List<TopWriterVO> list = mdao.getTopWriters();

        if (list == null || list.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // ✅전체 회원 수 요청에 따른 응답
    @GetMapping("/member/count")
    public ResponseEntity<Integer> fetchTotalMember() {
        MainDao mdao = new MainDao();
        int count = mdao.getTotalMemberCount();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    // ✅오늘 새로 등록된 글 수 요청에 따른 응답
    @GetMapping("post/today-count")
    public ResponseEntity<Integer> fetchTodayPostCount() {
        MainDao mdao = new MainDao();
        int count = mdao.getTodayPostCount();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    // ✅오늘 새로 등록된 댓글 수 요청에 따른 응답
    @GetMapping("reply/today-count")
    public ResponseEntity<Integer> fetchTodayReplyCount() {
        MainDao mdao = new MainDao();
        int count = mdao.getTodayReplyCount();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    // ✅포트폴리오 게시판 글 전체 수 요청에 따른 응답
    @GetMapping("post/portfolio-count")
    public ResponseEntity<Integer> fetchPortfolioPostCount() {
        MainDao mdao = new MainDao();
        int count = mdao.getPortfolioPostCount();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    // ✅전체 글 수 요청에 따른 응답
    @GetMapping("post/count")
    public ResponseEntity<Integer> fetchTotalPostCount() {
        MainDao mdao = new MainDao();
        int count = mdao.getTotalPostCount();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    // ✅각 게시판 별 최근 게시글 요청에 따른 응답
    @GetMapping("/post/latest/{boardNum}")
    public ResponseEntity<List<PostInfoVO>> fetchLatestPosts(@PathVariable int boardNum) {
        MainDao mdao = new MainDao();
        List<PostInfoVO> list = mdao.getLatestPosts(boardNum);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // ✅️회원 프로필 사진(by email) 요청에 따른 응답
    @PostMapping("/member/pfImg")
    public ResponseEntity<String> fetchProfileImage(@RequestBody Map<String, String> emailData) {
        String email = emailData.get("email");
        MainDao mdao = new MainDao();
        String pfImg = mdao.getProfileImageByEmail(email);

        if (pfImg == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(pfImg, HttpStatus.OK);
    }

    // ✅회원 프로필 사진(by memberNum) 요청에 따른 응답
    @PostMapping("/memberNum/pfImg")
    public ResponseEntity<String> fetchPfImgByMemberNum(@RequestBody Map<String, Integer> memberNumData) {
        int memberNum = memberNumData.get("memberNum");
        MainDao mdao = new MainDao();
        String pfImg = mdao.getProfileImageByMemberNum(memberNum);

        if (pfImg == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } return new ResponseEntity<>(pfImg, HttpStatus.OK);
    }

    // ✅회원 닉네임(by memberNum) 요청에 따른 응답
    @PostMapping("/memberNum/nickname")
    public ResponseEntity<String> fetchNicknameByMemberNum(@RequestBody Map<String, Integer> memberNumData) {
        int memberNum = memberNumData.get("memberNum");
        MainDao mdao = new MainDao();
        String nickname = mdao.getNicknameByMemberNum(memberNum);

        if (nickname == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } return new ResponseEntity<>(nickname, HttpStatus.OK);
    }

    // ✅메인 검색 결과 요청에 따른 응답
    @GetMapping("/main/search")
    public ResponseEntity<List<PostInfoVO>> mainSearchPosts(@RequestParam("keyword") String keyword) {
        MainDao mdao = new MainDao();
        List<PostInfoVO> list = mdao.mainSearchPosts(keyword);

        if (list != null || !list.isEmpty()) {
            return new ResponseEntity<>(list, HttpStatus.OK);
        } return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // 🔴️회원 닉네임 요청에 따른 응답
    @PostMapping("/member/nickname")
    public ResponseEntity<String> fetchNickname(@RequestBody Map<String, String> emailData) {
        String email = emailData.get("email");
        MainDao mdao = new MainDao();
        String nickname = mdao.getNickNameByEmail(email);

        if (nickname == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(nickname, HttpStatus.OK);
    }
}
