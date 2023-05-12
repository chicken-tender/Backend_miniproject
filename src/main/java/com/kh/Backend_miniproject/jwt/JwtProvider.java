package com.kh.Backend_miniproject.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

@Component
public class JwtProvider {
    @Value("${jwt.secret}")
    private String secret;

    // 토큰 생성 메소드
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 1일 유효
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    // 토큰에서 사용자명 추출 메소드
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 토큰 유효성 검증 메소드
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        System.out.println("토큰에서 추출한 사용자 이름: " + username);
        System.out.println("UserDetails의 사용자 이름: " + userDetails.getUsername());

        boolean isTokenExpired = isTokenExpired(token);
        System.out.println("토큰이 만료되었는지: " + isTokenExpired);

        return (username.equals(userDetails.getUsername()) && !isTokenExpired);
    }


    // 토큰에서 정보 추출 메소드
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 토큰에서 모든 정보 추출 메소드
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    // 토큰 만료 여부 확인 메소드
    private Boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
