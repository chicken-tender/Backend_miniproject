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


// 일반 게시판 목록 조회
//    @GetMapping("/board/{boardNum}/{pageNum}")
//     public ResponseEntity<List<PostVO>> getGeneralPostList(@PathVariable("boardNum") int boardNum, @PathVariable("pageNum") int pageNum) {
//
//        BoardDAO dao = new BoardDAO();
//        List<PostVO> list = dao.generalPostList(boardNum,pageNum);
//     return new ResponseEntity<>(list, HttpStatus.OK);

    @GetMapping("/{boardName}/{pageNum}")
    public ResponseEntity<List<PostVO>> getGeneralPostList(@PathVariable("boardName") String boardName, @PathVariable("pageNum") int pageNum) {
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


    // 포토폴리오게시판 목록 조회
    @GetMapping("/Portfolio/{pageNum}")
    public ResponseEntity<List<PostVO>> fetchPortfolioList(@PathVariable("pageNum") int pageNum) {
        BoardDAO dao = new BoardDAO();
        List<PostVO> list = dao.portfolioList(pageNum);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    //베스트게시판 이동
    @PostMapping("/board/updateBestBoard")
    public ResponseEntity<String> fetchUpdateBestBoard() {
        BoardDAO dao = new BoardDAO();
        dao.updateBestBoard();
        return new ResponseEntity<>("true", HttpStatus.OK);
    }


    // 게시판 검색
    @GetMapping("/board/search")
    public ResponseEntity<List<PostVO>> fetchSearchPosts(@RequestParam("boardNum") int boardNum, @RequestParam("pageNum") int pageNum, @RequestParam("keyword") String keyword) {
        BoardDAO dao = new BoardDAO();
        List<PostVO> list = dao.searchPosts(boardNum, pageNum, keyword);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

        // 상세글 보기
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

    // 댓글 상세보기
    @GetMapping("/post/{postNum}/reply")
    public ResponseEntity<List<ReplyVO>> fetchViewReply(@PathVariable("postNum") int postNum) {
        BoardDAO dao = new BoardDAO();
        List<ReplyVO> replies = dao.viewReply(postNum);
        if (replies.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(replies, HttpStatus.OK);
    }


    // 조회수 증가
    @GetMapping("/post/{postNum}/increaseViews")
    public ResponseEntity<String> fetchIncreaseViews(@PathVariable int postNum) {
        BoardDAO dao = new BoardDAO();
        int result = dao.increaseViews(postNum);
        if (result > 0) {
            return new ResponseEntity<>("true", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("false", HttpStatus.NOT_FOUND);
        }
    }

    // 게시글 작성
    @PostMapping("/write")
    public ResponseEntity<String> fetchWritePost(@RequestBody PostVO post) {
        BoardDAO dao = new BoardDAO();
        dao.writePost(post);
        return new ResponseEntity<>("True", HttpStatus.OK);
    }

    // 댓글 작성
    @PostMapping("/writeReply")
    public ResponseEntity<String> fetchWriteReply(@RequestParam("postNum") int postNum, @RequestParam("memberNum") int memberNum, @RequestParam("content") String content) {
        BoardDAO dao = new BoardDAO();
        dao.writeReply(postNum, memberNum, content);
        return new ResponseEntity<>("True", HttpStatus.OK);
    }
}

