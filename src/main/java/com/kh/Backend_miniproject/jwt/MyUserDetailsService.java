package com.kh.Backend_miniproject.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private DataSource dataSource;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String sql = "SELECT * FROM MEMBERS_TB WHERE EMAIL = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String sqlEmail = rs.getString("EMAIL");
                    String sqlPwd = rs.getString("PWD");
                    System.out.println(("🍎 : " + User.withUsername(sqlEmail)
                            .password(sqlPwd)
                            .roles("USER")
                            .build()));
                    return User.withUsername(sqlEmail)
                            .password(sqlPwd)
                            .roles("USER")
                            .build();
                } else {
                    throw new UsernameNotFoundException("User not found.");
                }
            }
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found.", e);
        }
    }
}
