package com.kh.Backend_miniproject.jwt;

import com.kh.Backend_miniproject.vo.MembersVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    @GetMapping("/user")
    public ResponseEntity<List<MembersVO>> getUserInfo() {
        UserDAO uda = new UserDAO();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        System.out.println("💙 : " + auth);
        System.out.println("🔥 : " + username);
        List<MembersVO> list = new ArrayList<>();

        list = uda.getUserInfoByEmail(username);
        if (list == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
