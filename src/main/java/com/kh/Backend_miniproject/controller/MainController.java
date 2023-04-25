package com.kh.Backend_miniproject.controller;
import com.kh.Backend_miniproject.dao.MainDao;
import com.kh.Backend_miniproject.vo.TopWriterVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class MainController {
    @GetMapping("/main/topwriter")
    public ResponseEntity<List<TopWriterVO>> getTopWritersInfo() {
        MainDao mdao = new MainDao();
        List<TopWriterVO> list = mdao.getTopWriters();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
