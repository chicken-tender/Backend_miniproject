package com.kh.Backend_miniproject.controller;


import com.kh.Backend_miniproject.dao.BoardDAO;
import com.kh.Backend_miniproject.vo.BoardVO;
import com.kh.Backend_miniproject.vo.PostVO;
import com.kh.Backend_miniproject.vo.ReplyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class BoardController {


    // ✏️일반 게시판 글 목록 조회
    @GetMapping("/{boardName}")
    public ResponseEntity<List<PostVO>> getGeneralPostList(@PathVariable("boardName") String boardName, @RequestParam("pageNum") int pageNum) {
        BoardDAO dao = new BoardDAO();
        List<BoardVO> boardNumList = dao.getBoardNum(boardName);
        List<PostVO> list = new ArrayList<>();
        for (BoardVO board : boardNumList) {
            int boardNum = board.getBoardNum();
            List<PostVO> postList = dao.generalPostList(boardNum, pageNum);
            list.addAll(postList);
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
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
    public ResponseEntity<List<PostVO>> fetchSearchPosts(@RequestParam("boardNum") int boardNum, @RequestParam("pageNum") int pageNum, @RequestParam("keyword") String keyword) {
        BoardDAO dao = new BoardDAO();
        List<PostVO> list = dao.searchPosts(boardNum, pageNum, keyword);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // ✏️ 상세글 보기
    @GetMapping("/{boardName}/post/{postNum}")
    public ResponseEntity<List<PostVO>> fetchViewPostDetail(@PathVariable("boardName") String boardName, @PathVariable("postNum") int postNum) {
        BoardDAO dao = new BoardDAO();
        List<BoardVO> boardNumList = dao.getBoardNum(boardName);
        if (boardNumList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        int boardNum = boardNumList.get(0).getBoardNum();
        List<PostVO> list = dao.viewPostDetail(boardNum, postNum);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // ✏️ 댓글 보기
    @GetMapping("/post/{postNum}/reply")
    public ResponseEntity<List<ReplyVO>> fetchViewReply(@PathVariable("postNum") int postNum) {
        BoardDAO dao = new BoardDAO();
        List<ReplyVO> replies = dao.viewReply(postNum);
        if (replies.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
    public ResponseEntity<String> fetchWritePost(@RequestBody PostVO post) {
        BoardDAO dao = new BoardDAO();
        dao.writePost(post);
        return new ResponseEntity<>("True", HttpStatus.OK);
    }

    // ✏️ 댓글 작성
    @PostMapping("/reply")
    public ResponseEntity<String> fetchWriteReply(@RequestBody Map<String, Object> contentData) {
        int postNum = (int) contentData.get("postNum");
        int memberNum = (int) contentData.get("memberNum");
        String content = (String) contentData.get("replyContent");

        BoardDAO dao = new BoardDAO();
        dao.writeReply(postNum, memberNum, content);
        return new ResponseEntity<>("True", HttpStatus.OK);
    }

    // ✏️ 댓글 수정
    @PostMapping("/reply/edit")
    public ResponseEntity<String> updateReply(@RequestBody Map<String, Object> payload) {
        int replyNum = (int) payload.get("replyNum");
        String content = (String) payload.get("content");

        BoardDAO dao = new BoardDAO();
        dao.updateReply(replyNum, content);
        return new ResponseEntity<>("True", HttpStatus.OK);
    }

    // ✏️댓글 삭제
    @DeleteMapping("/reply/{replyNum}")
    public ResponseEntity<String> deleteReply(@PathVariable int replyNum) {
        BoardDAO dao = new BoardDAO();
        dao.deleteReply(replyNum);
        return new ResponseEntity<>("True", HttpStatus.OK);
    }



    // ✏️게시글 수정
    @PostMapping("/post/edit")
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

    // ❤ 추천수 업데이트
    @PostMapping("/like/{postNum}")
    public ResponseEntity<Integer> fetchUpdateLikes(@PathVariable("postNum") int postNum, @RequestParam("memberNum") int memberNum) {
        BoardDAO dao = new BoardDAO();
        int result = dao.updateLikes(postNum, memberNum);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}

