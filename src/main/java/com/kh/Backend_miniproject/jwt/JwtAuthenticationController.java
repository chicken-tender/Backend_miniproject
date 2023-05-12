package com.kh.Backend_miniproject.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class JwtAuthenticationController {
    @Autowired
    private MyUserDetailsService userDetailsService;
    @Autowired
    private JwtProvider jwtProvider;

    @PostMapping("/auth")
    public ResponseEntity<String> createAuthToken(@RequestBody Map<String, String> authRequest) throws Exception {
        UserDetails userDetails = userDetailsService
                .loadUserByUsername(authRequest.get("email"));

        if (!userDetails.getPassword().equals(authRequest.get("pwd"))) {
            throw new BadCredentialsException("이메일, 비밀번호가 맞지 않습니다.");
        }
        final String jwt = jwtProvider.generateToken(userDetails);

        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }

}
