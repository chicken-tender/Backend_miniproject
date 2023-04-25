package com.kh.Backend_miniproject.controller;


import com.kh.Backend_miniproject.dao.BoardDAO;
import com.kh.Backend_miniproject.vo.PostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class BoardController {


// 일반 게시판 목록 조회
    @GetMapping("/Board/{boardNum}/{pageNum}")
     public ResponseEntity<List<PostVO>> getGeneralPostList(@PathVariable("boardNum") int boardNum, @PathVariable("pageNum") int pageNum) {
     System.out.println("Board Number : " + boardNum);
        System.out.println("Page Number : " + pageNum);
        BoardDAO dao = new BoardDAO();
        List<PostVO> list = dao.generalPostList(boardNum, pageNum);
     return new ResponseEntity<>(list, HttpStatus.OK);
}

    // 포토폴리오게시판 목록 조회
    @GetMapping("/Photofolio/{pageNum}")
    public ResponseEntity<List<PostVO>> getPhotofolioList(@PathVariable("pageNum") int pageNum) {
        BoardDAO dao = new BoardDAO();
        List<PostVO> list = dao.portfolioList(pageNum);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

}

