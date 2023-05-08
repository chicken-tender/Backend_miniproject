package com.kh.Backend_miniproject.controller;


import com.kh.Backend_miniproject.dao.AccountDAO;
import com.kh.Backend_miniproject.dao.BoardDAO;
import com.kh.Backend_miniproject.dao.MainDao;
import com.kh.Backend_miniproject.vo.BoardVO;
import com.kh.Backend_miniproject.vo.PostVO;
import com.kh.Backend_miniproject.vo.ReplyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class BoardController {


    // ✏️일반 게시판 글 목록 조회
    @GetMapping("/{boardName}")
    public ResponseEntity<List<PostVO>> getGeneralPostList(@PathVariable("boardName") String boardName, @RequestParam("pageNum") int pageNum) {
        BoardDAO dao = new BoardDAO();
        int boardNum = dao.getBoardNum(boardName);
        if (boardNum == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<PostVO> postList = dao.generalPostList(boardNum, pageNum);
        return new ResponseEntity<>(postList, HttpStatus.OK);
    }


    // ✏️포토폴리오게시판 목록 조회
    @GetMapping("/Portfolio")
    public ResponseEntity<List<PostVO>> fetchPortfolioList(@RequestParam("pageNum") int pageNum) {
        BoardDAO dao = new BoardDAO();
        List<PostVO> list = dao.portfolioList(pageNum);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // ✏️베스트 게시판으로 이동
    @PostMapping("/board/best")
    public ResponseEntity<String> fetchMoveBestBoard() {
        BoardDAO dao = new BoardDAO();
        dao.moveToBestBoard();
        return new ResponseEntity<>("true", HttpStatus.OK);
    }


    // ✏️게시판에서 검색
    @GetMapping("/search")
    public ResponseEntity<List<PostVO>> fetchSearchPosts(@RequestParam("boardName") String boardName, @RequestParam("pageNum") int pageNum, @RequestParam("keyword") String keyword) {
        BoardDAO dao = new BoardDAO();
        int boardNum = dao.getBoardNum(boardName);
        if (boardNum == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<PostVO> list = dao.searchPosts(boardNum, pageNum, keyword);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // ✏️ 상세글 보기
    @GetMapping("/post/{postNum}")
    public ResponseEntity<List<PostVO>> fetchViewPostDetail( @PathVariable("postNum") int postNum) {
        BoardDAO dao = new BoardDAO();
        List<PostVO> posts = dao.viewPostDetail(postNum);
        if (posts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }


    // ✏️ 댓글 보기
    @GetMapping("/reply")
    public ResponseEntity<List<ReplyVO>> fetchViewReply(@RequestParam("postNum") int postNum) {
        BoardDAO dao = new BoardDAO();
        List<ReplyVO> replies = dao.viewReply(postNum);
        if (replies.isEmpty()) {
            replies = new ArrayList<>();
        }
        return new ResponseEntity<>(replies, HttpStatus.OK);
    }


    // 👀조회수 증가
    @PostMapping("/post/{postNum}/views")
    public ResponseEntity<String> fetchIncreaseViews(@PathVariable int postNum) {
        BoardDAO dao = new BoardDAO();
        int result = dao.increaseViews(postNum);
        if (result > 0) {
            return new ResponseEntity<>("true", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("false", HttpStatus.NOT_FOUND);
        }
    }

    // ✏️ 게시글 작성
    @PostMapping("/post")
    public ResponseEntity<Integer> fetchWritePost(@RequestBody PostVO post) {
        BoardDAO dao = new BoardDAO();
        int postNum = dao.writePost(post);
        return new ResponseEntity<>(postNum, HttpStatus.OK);
    }


    // ✏️게시글 수정
    @PutMapping("/post")
    public ResponseEntity<String> updatePost(@RequestBody PostVO post) {
        BoardDAO dao = new BoardDAO();
        dao.updatePost(post);
        return new ResponseEntity<>("True", HttpStatus.OK);
    }


    // ✏️게시글 삭제
    @DeleteMapping("/post/{postNum}")
    public ResponseEntity<String> fetchDeletePost(@PathVariable int postNum) {
        BoardDAO dao = new BoardDAO();
        dao.deletePost(postNum);
        return new ResponseEntity<>("True", HttpStatus.OK);
    }


    // ✏️ 댓글 작성
    @PostMapping("/reply")
    public ResponseEntity<String> fetchWriteReply(@RequestBody Map<String, Object> data) {
        int postNum = Integer.parseInt((String) data.get("postNum"));
        int memberNum = (int) data.get("memberNum");
        String replyContent = (String) data.get("replyContent");
        BoardDAO dao = new BoardDAO();
        dao.writeReply(postNum, memberNum, replyContent);
        return new ResponseEntity<>("True", HttpStatus.OK);
    }

    // ✏️ 댓글 수정
    @PutMapping("/reply")
    public ResponseEntity<String> updateReply(@RequestBody Map<String, Object> data) {
        int replyNum = (int) data.get("replyNum");
        String replyContent = (String) data.get("content");

        BoardDAO dao = new BoardDAO();
        dao.updateReply(replyNum, replyContent);
        return new ResponseEntity<>("True", HttpStatus.OK);
    }


    // ✏️댓글 삭제
    @DeleteMapping("/reply")
    public ResponseEntity<String> deleteReply(@RequestParam("replyNum") int replyNum) {
        BoardDAO dao = new BoardDAO();
        dao.deleteReply(replyNum);
        return new ResponseEntity<>("True", HttpStatus.OK);
    }

     // ❤ 추천 상태 반환
    @GetMapping("/likeStatus")
    public ResponseEntity<Map<String, Object>> fetchLikesStatus(@RequestParam("postNum") int postNum, @RequestParam("memberNum") int memberNum) {
        BoardDAO dao = new BoardDAO();
        boolean currentLikesStatus = dao.getLikesStatus(postNum, memberNum);

        Map<String, Object> result = new HashMap<>();
        result.put("isLiked", currentLikesStatus);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // ❤ 추천수 업데이트
    @PostMapping("/like/{postNum}")
    public ResponseEntity<Boolean> fetchUpdateLikes(@PathVariable("postNum") int postNum, @RequestParam("memberNum") int memberNum) {
        BoardDAO dao = new BoardDAO();
        boolean TRUE = dao.updateLikes(postNum, memberNum);
        return new ResponseEntity<>(TRUE, HttpStatus.OK);
    }




    // 게시판 별 게시물 수 조회
    @GetMapping("/posts")
    public int fetchTotalPosts(@RequestParam("boardNum") int boardNum) {
        BoardDAO dao = new BoardDAO();
        return dao.getTotalPosts(boardNum);
    }

    // 검색결과에 따른 게시물 수 조회
    @GetMapping("/search/posts")
    public int fetchSearchTotal(@RequestParam("boardNum") int boardNum,
                               @RequestParam("keyword") String keyword) {
        BoardDAO dao = new BoardDAO();
        return dao.getSearchCount(boardNum, keyword);
    }

    // 이메일로 회원번호 get
    @PostMapping("/member/number")
    public ResponseEntity<Integer> fetchMemberNumber(@RequestBody Map<String, String> emailData) {
        String email = emailData.get("email");
        AccountDAO adao = new AccountDAO();
        int memberNum = adao.getMemberNumbyEmail(email);
        if (memberNum == 0) {
            return new ResponseEntity<>(memberNum, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(memberNum, HttpStatus.OK);
    }
}



