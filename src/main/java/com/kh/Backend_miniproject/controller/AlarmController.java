package com.kh.Backend_miniproject.controller;

import com.kh.Backend_miniproject.ReplyService;
import com.kh.Backend_miniproject.dao.AlarmDAO;
import com.kh.Backend_miniproject.vo.AlarmVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class AlarmController {
    @Autowired
    private ReplyService replyService;

    @GetMapping("/author-email")
    public ResponseEntity<String> fetchAuthorEmailByPostNum(@RequestParam int postNum) {
        AlarmDAO adao = new AlarmDAO();
        String authorEmail = adao.getAuthorEmailByPostNum(postNum);

        if(authorEmail != null) {
            return new ResponseEntity<>(authorEmail, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/reply-alarm")
    public ResponseEntity<String> writeReply(@RequestBody AlarmVO alarmVO) {
        try {
            replyService.writeReply(alarmVO.getPostNum(), alarmVO.getMemberNum(), alarmVO.getContent());
            return new ResponseEntity<>("알림 이메일 발송", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
