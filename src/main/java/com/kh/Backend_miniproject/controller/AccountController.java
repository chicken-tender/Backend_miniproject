package com.kh.Backend_miniproject.controller;
import com.kh.Backend_miniproject.dao.AccountDAO;
import com.kh.Backend_miniproject.vo.MyPageVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class AccountController {
    // 📍 POST : 로그인 - 경미 테스트
    @PostMapping("/login")
    public ResponseEntity<Boolean> loginUser(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String pwd = loginData.get("pwd");
        AccountDAO aDao = new AccountDAO();
        boolean result = aDao.isLoginValid(email, pwd);

        if (result) { // 입력한 email,pwd가 유효한 정보이면 'true'와 Http 상태 코드 '200'을 전달
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else { // 입력한 email, pwd가 유효하지 않은 정보이면 'false'와 Http 상태 코드 '401'을 전달
            return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
        }
    }

    // 📍 GET : 회원정보 조회
    @GetMapping("/member/information={memberNum}")
    public ResponseEntity<List<MyPageVO>> fetchMemberInfoByNumber(@PathVariable("memberNum") int memberNum) {
        AccountDAO dao = new AccountDAO();
        List<MyPageVO> list = dao.getMemberInfoByNum(memberNum);
        if (!list.isEmpty()) {
            return new ResponseEntity<>(list, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}

