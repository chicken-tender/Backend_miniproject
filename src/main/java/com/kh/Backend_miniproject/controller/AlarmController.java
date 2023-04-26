package com.kh.Backend_miniproject.controller;

import com.kh.Backend_miniproject.dao.AlarmDAO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class AlarmController {
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
}
