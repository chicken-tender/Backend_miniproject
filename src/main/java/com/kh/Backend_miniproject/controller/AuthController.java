package com.kh.Backend_miniproject.controller;

import com.kh.Backend_miniproject.AccountEmailService;
import com.kh.Backend_miniproject.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@Service
public class AuthController {

    @Autowired
    private final AccountEmailService emailService;

//    @PostMapping("/emailauth") // 이메일 인증 코드 보내기
//    public ResponseEntity<String> emailAuth(@RequestBody Map<String, String> email) throws Exception {
//        emailService.sendSimpleMessage(email.get("email"));
//        return new ResponseEntity<>("True", HttpStatus.OK);
//    }

//    @PostMapping("/emailauth") // 이메일 인증 코드 보내기
//    public ResponseEntity<String> emailAuth(@RequestBody Map<String, String> email) throws Exception {
//        String email = request.get("email");
//        emailService.sendSimpleMessage(email.get("email"));
//        return new ResponseEntity<>("True", HttpStatus.OK);
//    }

    @PostMapping("/verifyCode") // 이메일 인증 코드 검증
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> code) {
        if (emailService.getEPw().equals(code.get("code"))) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
                return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }
}



