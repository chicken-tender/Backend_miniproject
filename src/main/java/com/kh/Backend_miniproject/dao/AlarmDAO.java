package com.kh.Backend_miniproject.dao;
import com.kh.Backend_miniproject.common.Common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AlarmDAO {
    private Connection conn = null;
    private ResultSet rs = null;
    private PreparedStatement pstmt = null;

    /* 내가 작성한 글에 댓글이 달렸을 때 알리기 위함.
         -> ✨작성자의 이메일 조회
     */
    public String getAuthorEmailByPostNum(int postNum) {
        String email = null;
        String sql = "SELECT M.EMAIL " +
                "FROM MEMBERS_TB M, POST_TB P " +
                "WHERE P.POST_NUM_PK = ? " +
                "AND M.MEMBER_NUM_PK = P.MEMBER_NUM_FK";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, postNum);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                email = rs.getString("EMAIL");
            }
            Common.close(rs);
            Common.close(pstmt);
            Common.close(conn);

        } catch(Exception e) {
            e.printStackTrace();
        }
        return email;
    }
}
