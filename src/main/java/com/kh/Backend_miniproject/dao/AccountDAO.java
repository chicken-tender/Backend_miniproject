package com.kh.Backend_miniproject.dao;
import com.kh.Backend_miniproject.common.Common;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class AccountDAO {
    private Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement pstmt = null;
    private ResultSet rs = null;

    // 📍로그인 체크 - 경미 테스트
    public boolean isLoginValid(String email, String pwd) {
        String sql = "SELECT * FROM MEMBERS_TB WHERE EMAIL = " + "'" + email + "'";
        try {
            conn = Common.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while(rs.next()) {
                String sqlEmail = rs.getString("EMAIL");
                String sqlPwd = rs.getString("PWD");
                if(email.equals(sqlEmail) && pwd.equals(sqlPwd)) {
                    Common.close(rs);
                    Common.close(stmt);
                    Common.close(conn);
                    return true;
                }
            }
            Common.close(rs);
            Common.close(stmt);
            Common.close(conn);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
