package com.kh.Backend_miniproject.controller;
import com.kh.Backend_miniproject.dao.AccountDAO;
import com.kh.Backend_miniproject.dao.MainDao;
import com.kh.Backend_miniproject.vo.MembersVO;
import com.kh.Backend_miniproject.vo.PostInfoVO;
import com.kh.Backend_miniproject.vo.TopWriterVO;
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

    // ✅정보공유 게시판 최근 게시글 5개 요청에 따른 응답
    @GetMapping("post/information-latest-5")
    public ResponseEntity<List<PostInfoVO>> fetchLatestInformationPosts() {
        MainDao mdao = new MainDao();
        List<PostInfoVO> list = mdao.getLatestInformationPosts();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // ✅포트폴리오 게시판 최근 게시글 5개 요청에 따른 응답
    @GetMapping("post/portfolio-latest-5")
    public ResponseEntity<List<PostInfoVO>> fetchLatestPortfolioPosts() {
        MainDao mdao = new MainDao();
        List<PostInfoVO> list = mdao.getLatestPortfolioPosts();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // ✅베스트 게시판 최근 게시글 5개 요청에 따른 응답
    @GetMapping("post/best-latest-5")
    public ResponseEntity<List<PostInfoVO>> fetchLatestBestPosts() {
        MainDao mdao = new MainDao();
        List<PostInfoVO> list = mdao.getLatestBestPosts();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // ✅Q&A 게시판 최근 게시글 5개 요청에 따른 응답
    @GetMapping("post/qna-latest-5")
    public ResponseEntity<List<PostInfoVO>> fetchLatestQnAPosts() {
        MainDao mdao = new MainDao();
        List<PostInfoVO> list = mdao.getLatestQnAPosts();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // ✅️회원 프로필 사진 요청에 따른 응답
    @PostMapping("/member/pfImg")
    public ResponseEntity<String> fetchProfileImage(@RequestBody Map<String, String> emailData) {
        String email = emailData.get("email");
        MainDao mdao = new MainDao();
        String pfImg = mdao.getProfileImageByEmail(email);

        if (pfImg == null) {
            return new ResponseEntity<>(pfImg, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(pfImg, HttpStatus.OK);
    }

    // ❌️회원 닉네임 요청에 따른 응답
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
